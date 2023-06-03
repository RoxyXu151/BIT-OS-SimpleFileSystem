import java.io.*;
import java.util.Scanner;

public class SimpleFileSystem {

    private static Directory root = new Directory("/", true, null); // 根目录
    private final Scanner in;           // 命令接受器
    private Directory currentDir;       // 当前目录项
    private String currentPath;         // 当前路径
    private VirtualDisk vDisk;          // 虚拟磁盘

    public SimpleFileSystem() {
        this.currentDir = root;
        this.currentPath = "/";
        this.vDisk = new VirtualDisk("/dev/sda");
        this.in = new Scanner(System.in);
        File f = new File(DiskConst.SerializePath);
        if (f.exists()) {
            loadFS(DiskConst.SerializePath);
        }
    }

    public void loadFS(String path) {
        try {
            FileInputStream in = new FileInputStream(path);
            ObjectInputStream iin = new ObjectInputStream(in);
            this.vDisk = (VirtualDisk) iin.readObject();
            root = (Directory) iin.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveFS(String path) {
        try {
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this.vDisk);
            out.writeObject(root);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 创建目录
    private void mkdir(String dirName) {
        if (dirName.equals(".") || dirName.equals("..") || dirName.contains("/")) {
            // 文件名不能为"."或者".."，并且不能包含"/"
            System.out.println("Failed: Invalid directory name!");
            System.out.println("Directory name should not be \".\" or contains \"/\".");
        } else if (currentDir.dirTable.containsKey(dirName)) {
            // 检查同名文件夹或者文件是否已经存在
            System.out.println("Failed: " + dirName + " is already existed.");
        } else {
            // 当以上条件均正确无误时将目录项添加至 dirTable 中
            currentDir.dirTable.put(dirName, new Directory(dirName, true, currentDir));
            System.out.println(dirName + " is created.");
        }
    }

    // 删除目录
    private void rmdir(String dirName) {
        // TODO：使用栈来简化
        // 暂存当前目录以及文件夹
        Directory stashCurrDir = currentDir;
        String stashCurrentPath = currentPath;
        // 目录名称存在并且类型为目录
        if (currentDir.dirTable.containsKey(dirName) && currentDir.dirTable.get(dirName).isDirectory) {
            Directory tmpDir = (Directory) currentDir.dirTable.get(dirName);
            if (tmpDir.dirTable.isEmpty()) {
                // 当文件目录为空时，可以删除当前目录
                currentDir.dirTable.remove(dirName);
                System.out.println(dirName + " is removed.");
            } else {
                // 当文件夹不为空时，询问是否要递归删除全部内容
                System.out.println("""
                        Failed: Current directory is not empty.
                        Do you want to remove all files and folders in this folder?
                        Enter "Y" or "y" to continue, otherwise nothing will be changed.""");
                String answer = in.next();
                if (answer.equals("Y") || answer.equals("y")) {
                    // 确认清理
                    cd("./" + dirName);
                    for (String fName : currentDir.dirTable.keySet()) {
                        if (currentDir.dirTable.get(fName).isDirectory) {
                            // 递归删除文件夹
                            r_rmdir(fName);
                        } else {
                            // 递归删除文件
                            deleteFile(fName);
                        }
                    }
                    cd("./..");
                    currentDir.dirTable.remove(dirName);
                    System.out.println(dirName + " is removed.");
                } else {
                    // 不清理
                    System.out.println("Nothing will be changed.");
                }
            }
        } else {
            System.out.println("Failed: " + dirName + " is not existed.");
        }
        // 恢复目录
        currentDir = stashCurrDir;
        currentPath = stashCurrentPath;
    }

    private void r_rmdir(String dirName) {
        // 递归删除器
        // 暂存当前目录以及文件夹
        Directory stashCurrDir = currentDir;
        String stashCurrentPath = currentPath;
        // 目录名称存在并且类型为目录
        if (currentDir.dirTable.containsKey(dirName) && currentDir.dirTable.get(dirName).isDirectory) {
            Directory tmpDir = (Directory) currentDir.dirTable.get(dirName);
            if (tmpDir.dirTable.isEmpty()) {
                // 当文件目录为空时，可以删除当前目录
                currentDir.dirTable.remove(dirName);
                System.out.println(dirName + " is removed.");
            } else {
                // 递归清理
                cd("./" + dirName);
                for (String fName : currentDir.dirTable.keySet()) {
                    if (currentDir.dirTable.get(fName).isDirectory) {
                        // 递归删除文件夹
                        r_rmdir(fName);
                    } else {
                        // 递归删除文件
                        deleteFile(fName);
                    }
                }
                cd("./..");
                currentDir.dirTable.remove(dirName);
                System.out.println(dirName + " is removed.");
            }
        } else {
            System.out.println("Failed: " + dirName + " is not existed.");
        }
        // 恢复目录
        currentDir = stashCurrDir;
        currentPath = stashCurrentPath;
    }

    // 显示当前目录下所有目录项
    private void ls() {
        pwd();
        // 目录为紫色；文件为浅蓝色。
        for (String name : currentDir.dirTable.keySet()) {
            if (currentDir.dirTable.get(name).isDirectory) {
                System.out.println("\033[35;4m" + name + "\033[0m");
            } else {
                System.out.println("\033[36;4m" + name + "\033[0m");
            }
        }
    }

    // 显示当前目录地址
    private void pwd() {
        int reduceLength = 0;
        // 对是否在根目录下进行特别判断
        if (currentPath.length() > 1) {
            reduceLength = 1;
        }
        // 输出当前路径
        System.out.println("\033[32;4m" + "Current Path: " +
                currentPath.substring(0, currentPath.length() - reduceLength) + "\033[0m");
    }

    // 改变当前目录
    private void cd(String path) {
        boolean parseFinished = true;                       // 判断解析是否全部完成
        boolean correctCmd = false;                         // 判断输入命令是否符合格式
        Directory tmpCurrDir = null;                        // 临时Dir变量
        StringBuilder tmpPath = null;                       // 临时Path变量
        String dirName;                                     // 临时DirName变量
        String[] directories = path.split("/");       // 地址分割成块
        // 对返回根目录进行特判
        if (path.equals("/")) {
            currentDir = root;
            currentPath = "/";
        } else {
            // 判断是从当前目录开始还是从根目录开始解析地址
            if (directories[0].equals(".")) {
                correctCmd = true;
                tmpCurrDir = currentDir;
                tmpPath = new StringBuilder(currentPath);
            } else if (directories[0].equals("")) {
                correctCmd = true;
                tmpCurrDir = root;
                tmpPath = new StringBuilder("/");
            }
            // 若命令格式正确则开始解析路径
            if (correctCmd) {
                for (int i = 1; i < directories.length; i++) {
                    dirName = directories[i];
                    // 文件目录项存在且类型为目录，这里利用了短路运算
                    if (tmpCurrDir.dirTable.containsKey(dirName) && tmpCurrDir.dirTable.get(dirName).isDirectory) {
                        // 访问下一层
                        tmpCurrDir = (Directory) tmpCurrDir.dirTable.get(dirName);
                        tmpPath.append(dirName).append("/");
                    } else if (dirName.equals("..") && tmpCurrDir.parentDir != null) {
                        // 访问上一层
                        tmpCurrDir = tmpCurrDir.parentDir;
                        tmpPath = new StringBuilder(tmpPath.substring(0, tmpPath.length() - currentDir.name.length() - 1));
                    } else {
                        // 错误处理
                        parseFinished = false;
                        System.out.println("Failed: " + tmpPath + dirName + " is not existed");
                        break;
                    }
                }
                if (parseFinished) {
                    currentDir = tmpCurrDir;
                    currentPath = tmpPath.toString();
                }
            } else {
                System.out.println("Failed: Invalid cd command.The first character should be \"/\" or \".\"");
            }

        }
    }

    private void createFile(String fileName) {
        if (fileName.equals(".") || fileName.equals("..") || fileName.contains("/")) {
            // 文件名不能为"."或者".."，并且不能包含"/"
            System.out.println("Failed: Invalid directory name!");
            System.out.println("Directory name should not be \".\" or contains \"/\".");
        } else if (currentDir.dirTable.containsKey(fileName)) {
            // 检查同名文件夹或者文件是否已经存在
            System.out.println("Failed: " + fileName + " is already existed.");
        } else {
            // 当以上条件均正确无误时将目录项添加至 dirTable 中
            // 使用Lazy分配策略，创建文件时不分配空间，文件写入时分配空间，这里仅需要添加目录项目即可。
            currentDir.dirTable.put(fileName, new DirectoryFile(fileName, false, currentDir));
            System.out.println(fileName + " is created.");
        }
    }

    private void deleteFile(String fileName) {
        if (currentDir.dirTable.containsKey(fileName) && !currentDir.dirTable.get(fileName).isDirectory) {
            // 回收空间
            DirectoryFile tmpFile = (DirectoryFile) currentDir.dirTable.get(fileName);
            if (tmpFile.file.fileClear(this.vDisk)) {
                // 删除文件目录项
                currentDir.dirTable.remove(fileName);
                System.out.println(fileName + " is removed.");
            }
        } else {
            System.out.println("Failed: " + fileName + " is not existed.");
        }
    }

    private void openFile(String fileName) {
        if (currentDir.dirTable.containsKey(fileName) && !currentDir.dirTable.get(fileName).isDirectory) {
            // 文件存在时
            // 文件标记为打开
            DirectoryFile tmpFile = (DirectoryFile) currentDir.dirTable.get(fileName);
            // 从磁盘中读取buffer
            tmpFile.file.setOpen(true, this.vDisk);
            System.out.println(fileName + " is open.");
        } else {
            System.out.println("Failed: " + fileName + " is not existed.");
        }
    }

    private void closeFile(String fileName) {
        if (currentDir.dirTable.containsKey(fileName) && !currentDir.dirTable.get(fileName).isDirectory) {
            // 文件存在时
            // 文件标记为关闭
            DirectoryFile tmpFile = (DirectoryFile) currentDir.dirTable.get(fileName);
            // 将 buffer 写入磁盘中
            tmpFile.file.setOpen(false, this.vDisk);
            System.out.println(fileName + " is closed.");
        } else {
            System.out.println("Failed: " + fileName + " is not existed.");
        }
    }

    // read && write 均在 buffer 中进行
    private String readFile(String fileName) {
        String buffer = null;
        if (currentDir.dirTable.containsKey(fileName) && !currentDir.dirTable.get(fileName).isDirectory) {
            // 文件存在时
            System.out.println("Loading...");
            DirectoryFile tmpFile = (DirectoryFile) currentDir.dirTable.get(fileName);
            // 当文件打开时
            if (tmpFile.file.isOpen()) {
                buffer = tmpFile.file.getContent();
            } else {
                System.out.println("Failed: " + fileName + " is not opened.");
            }
        } else {
            System.out.println("Failed: " + fileName + " is not existed.");
        }
        return buffer;
    }

    private void writeFile(String fileName, String buf, String mode) {
        if (currentDir.dirTable.containsKey(fileName) && !currentDir.dirTable.get(fileName).isDirectory) {
            // 文件存在时
            System.out.println("Loading...");
            DirectoryFile tmpFile = (DirectoryFile) currentDir.dirTable.get(fileName);
            // 当文件打开时
            if (tmpFile.file.isOpen() && (mode.equals("a") || mode.equals("w"))) {
                tmpFile.file.setContent(buf, mode);
                System.out.println(fileName + " has been written.");
            } else if (tmpFile.file.isOpen() && !(mode.equals("a") || mode.equals("w"))) {
                System.out.println("Failed: Invalid mode " + mode + ".");
            } else {
                System.out.println("Failed: " + fileName + " is not opened.");
            }
        } else {
            System.out.println("Failed: " + fileName + " is not existed.");
        }
    }

    public void run() {
        label:
        while (true) {
            System.out.print("fs> ");
            String command = in.next();
            String args;
            String buf;
            String rwMode;
            switch (command) {
                case "exit":
                    // 退出文件系统
                    System.out.println("Exiting the VirtualFile System.");
                    saveFS(DiskConst.SerializePath);
                    break label;
                case "mkdir":
                    // 创建目录
                    args = in.next();
                    mkdir(args);
                    break;
                case "rmdir":
                    // 删除目录
                    args = in.next();
                    rmdir(args);
                    break;
                case "ls":
                    // 显示当前目录下所有文件和目录
                    ls();
                    break;
                case "pwd":
                    // 显示当前路径
                    pwd();
                    break;
                case "cd":
                    // 切换路径
                    args = in.next();
                    cd(args);
                    break;
                case "create":
                    // 新建文件
                    args = in.next();
                    createFile(args);
                    break;
                case "delete":
                    // 删除文件
                    args = in.next();
                    deleteFile(args);
                    break;
                case "open":
                    // 打开文件文件
                    args = in.next();
                    openFile(args);
                    break;
                case "close":
                    // 关闭文件
                    args = in.next();
                    closeFile(args);
                    break;
                case "read":
                    // 读取文件
                    args = in.next();
                    buf = readFile(args);
                    if (buf != null) {
                        System.out.println(args + " contents:\n" + buf);
                    } else {
                        System.out.println("Failed: Read failed. Nothing will be changed.");
                    }
                    break;
                case "write":
                    args = in.next();
                    buf = in.next();
                    rwMode = in.next();
                    writeFile(args, buf, rwMode);
                    break;
                default:
                    System.out.println("Invalid command.");
            }
        }
    }
}
