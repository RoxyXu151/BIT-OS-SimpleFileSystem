import java.io.*;
import java.util.Scanner;

/**
 * 简易文件系统实现类
 * 提供文件和目录管理的核心功能
 */
public class SimpleFileSystem {

    // 文件系统根目录
    private static Directory rootDirectory = new Directory("/", true, null); 
    // 用户输入处理器
    private final Scanner commandScanner;           
    // 当前工作目录
    private Directory workingDirectory;       
    // 当前工作路径
    private String workingPath;         
    // 存储设备实例
    private VirtualDisk storageDevice;          

    /**
     * 创建新的文件系统实例
     * 如果存在持久化数据，则加载现有文件系统状态
     */
    public SimpleFileSystem() {
        this.workingDirectory = rootDirectory;
        this.workingPath = "/";
        this.storageDevice = new VirtualDisk("/dev/sda");
        this.commandScanner = new Scanner(System.in);
        
        // 检查是否存在持久化的文件系统数据
        File persistenceFile = new File(DiskConst.SerializePath);
        if (persistenceFile.exists()) {
            restoreFileSystem(DiskConst.SerializePath);
            workingDirectory = rootDirectory;
        }
    }

    /**
     * 从持久化存储中恢复文件系统状态
     * @param filePath 持久化文件路径
     */
    public void restoreFileSystem(String filePath) {
        try {
            FileInputStream fileIn = new FileInputStream(filePath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            this.storageDevice = (VirtualDisk) objectIn.readObject();
            rootDirectory = (Directory) objectIn.readObject();
            objectIn.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to restore file system: " + e.getMessage(), e);
        }
    }

    /**
     * 将文件系统状态保存到持久化存储
     * @param filePath 持久化文件路径
     */
    public void persistFileSystem(String filePath) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(this.storageDevice);
            objectOut.writeObject(rootDirectory);
            objectOut.close();
            fileOut.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to persist file system: " + e.getMessage(), e);
        }
    }

    /**
     * 创建新目录
     * @param directoryName 目录名称
     */
    public void mkdir(String directoryName) {
        // 验证目录名是否合法
        if (directoryName.equals(".") || directoryName.equals("..") || directoryName.contains("/")) {
            // 文件名不能为"."或者".."，并且不能包含"/"
            System.out.println("Failed: Invalid directory name!");
            System.out.println("Directory name should not be \".\" or contains \"/\".");
        } else if (workingDirectory.dirTable.containsKey(directoryName)) {
            // 检查同名文件夹或者文件是否已经存在
            System.out.println("Failed: " + directoryName + " is already existed.");
        } else {
            // 当以上条件均正确无误时将目录项添加至 dirTable 中
            workingDirectory.dirTable.put(directoryName, new Directory(directoryName, true, workingDirectory));
            System.out.println(directoryName + " is created.");
        }
    }

    /**
     * 删除目录
     * @param directoryName 目录名称
     */
    public void rmdir(String directoryName) {
        // TODO：使用栈来简化
        // 暂存当前目录以及文件夹
        Directory stashCurrDir = workingDirectory;
        String stashCurrentPath = workingPath;
        // 目录名称存在并且类型为目录
        if (workingDirectory.dirTable.containsKey(directoryName) && workingDirectory.dirTable.get(directoryName).isDirectory) {
            Directory tmpDir = (Directory) workingDirectory.dirTable.get(directoryName);
            if (tmpDir.dirTable.isEmpty()) {
                // 当文件目录为空时，可以删除当前目录
                workingDirectory.dirTable.remove(directoryName);
                System.out.println(directoryName + " is removed.");
            } else {
                // 当文件夹不为空时，询问是否要递归删除全部内容
                System.out.println("""
                        Failed: Current directory is not empty.
                        Do you want to remove all files and folders in this folder?
                        Enter "Y" or "y" to continue, otherwise nothing will be changed.""");
                String answer = commandScanner.next();
                if (answer.equals("Y") || answer.equals("y")) {
                    // 确认清理
                    cd("./" + directoryName);
                    for (String fName : workingDirectory.dirTable.keySet()) {
                        if (workingDirectory.dirTable.get(fName).isDirectory) {
                            // 递归删除文件夹
                            r_rmdir(fName);
                        } else {
                            // 递归删除文件
                            deleteFile(fName);
                        }
                    }
                    cd("./..");
                    workingDirectory.dirTable.remove(directoryName);
                    System.out.println(directoryName + " is removed.");
                } else {
                    // 不清理
                    System.out.println("Nothing will be changed.");
                }
            }
        } else {
            System.out.println("Failed: " + directoryName + " is not existed.");
        }
        // 恢复目录
        workingDirectory = stashCurrDir;
        workingPath = stashCurrentPath;
    }

    /**
     * 递归删除目录
     * @param directoryName 目录名称
     */
    private void r_rmdir(String directoryName) {
        // 递归删除器
        // 暂存当前目录以及文件夹
        Directory stashCurrDir = workingDirectory;
        String stashCurrentPath = workingPath;
        // 目录名称存在并且类型为目录
        if (workingDirectory.dirTable.containsKey(directoryName) && workingDirectory.dirTable.get(directoryName).isDirectory) {
            Directory tmpDir = (Directory) workingDirectory.dirTable.get(directoryName);
            if (tmpDir.dirTable.isEmpty()) {
                // 当文件目录为空时，可以删除当前目录
                workingDirectory.dirTable.remove(directoryName);
                System.out.println(directoryName + " is removed.");
            } else {
                // 递归清理
                cd("./" + directoryName);
                for (String fName : workingDirectory.dirTable.keySet()) {
                    if (workingDirectory.dirTable.get(fName).isDirectory) {
                        // 递归删除文件夹
                        r_rmdir(fName);
                    } else {
                        // 递归删除文件
                        deleteFile(fName);
                    }
                }
                cd("./..");
                workingDirectory.dirTable.remove(directoryName);
                System.out.println(directoryName + " is removed.");
            }
        } else {
            System.out.println("Failed: " + directoryName + " is not existed.");
        }
        // 恢复目录
        workingDirectory = stashCurrDir;
        workingPath = stashCurrentPath;
    }

    /**
     * 显示当前目录下所有目录项
     */
    public void ls() {
        pwd();
        // 目录为紫色；文件为浅蓝色。
        for (String name : workingDirectory.dirTable.keySet()) {
            if (workingDirectory.dirTable.get(name).isDirectory) {
                System.out.println("\033[35;4m" + name + "\033[0m");
            } else {
                System.out.println("\033[36;4m" + name + "\033[0m");
            }
        }
    }

    /**
     * 显示当前目录地址
     */
    public void pwd() {
        int reduceLength = 0;
        // 对是否在根目录下进行特别判断
        if (workingPath.length() > 1) {
            reduceLength = 1;
        }
        // 输出当前路径
        System.out.println("\033[32;4m" + "Current Path: " +
                workingPath.substring(0, workingPath.length() - reduceLength) + "\033[0m");
    }

    /**
     * 切换当前工作目录
     * @param path 目标路径，可以是绝对路径或相对路径
     */
    public void cd(String path) {
        boolean parseFinished = true;                       // 判断解析是否全部完成
        boolean correctCmd = false;                         // 判断输入命令是否符合格式
        Directory tmpCurrDir = null;                        // 临时Dir变量
        StringBuilder tmpPath = null;                       // 临时Path变量
        String dirName;                                     // 临时DirName变量
        String[] directories = path.split("/");       // 地址分割成块
        // 对返回根目录进行特判
        if (path.equals("/")) {
            workingDirectory = rootDirectory;
            workingPath = "/";
        } else {
            // 判断是从当前目录开始还是从根目录开始解析地址
            if (directories[0].equals(".")) {
                correctCmd = true;
                tmpCurrDir = workingDirectory;
                tmpPath = new StringBuilder(workingPath);
            } else if (directories[0].equals("")) {
                correctCmd = true;
                tmpCurrDir = rootDirectory;
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
                        tmpPath = new StringBuilder(tmpPath.substring(0, tmpPath.length() - workingDirectory.name.length() - 1));
                    } else {
                        // 错误处理
                        parseFinished = false;
                        System.out.println("Failed: " + tmpPath + dirName + " is not existed");
                        break;
                    }
                }
                if (parseFinished) {
                    workingDirectory = tmpCurrDir;
                    workingPath = tmpPath.toString();
                }
            } else {
                System.out.println("Failed: Invalid cd command.The first character should be \"/\" or \".\"");
            }

        }
    }

    /**
     * 创建新文件
     * @param fileName 文件名称
     */
    private void createFile(String fileName) {
        if (fileName.equals(".") || fileName.equals("..") || fileName.contains("/")) {
            // 文件名不能为"."或者".."，并且不能包含"/"
            System.out.println("Failed: Invalid directory name!");
            System.out.println("Directory name should not be \".\" or contains \"/\".");
        } else if (workingDirectory.dirTable.containsKey(fileName)) {
            // 检查同名文件夹或者文件是否已经存在
            System.out.println("Failed: " + fileName + " is already existed.");
        } else {
            // 当以上条件均正确无误时将目录项添加至 dirTable 中
            // 使用Lazy分配策略，创建文件时不分配空间，文件写入时分配空间，这里仅需要添加目录项目即可。
            workingDirectory.dirTable.put(fileName, new DirectoryFile(fileName, false, workingDirectory));
            System.out.println(fileName + " is created.");
        }
    }

    /**
     * 删除文件
     * @param fileName 文件名称
     */
    private void deleteFile(String fileName) {
        if (fileName.equals(".") || fileName.equals("..") || fileName.contains("/")) {
            // 文件名不能为"."或者"..",并且不能包含"/"
            System.out.println("错误：无效的文件名！");
            System.out.println("文件名不能为\".\"或\"..\",且不能包含\"/\"");
        } else if (!workingDirectory.dirTable.containsKey(fileName)) {
            // 检查文件是否存在
            System.out.println("错误：文件 '" + fileName + "' 不存在");
        } else if (workingDirectory.dirTable.get(fileName).isDirectory) {
            // 检查是否为文件
            System.out.println("错误：'" + fileName + "' 是目录而非文件");
        } else {
            // 当以上条件均正确无误时将目录项从 dirTable 中移除
            DirectoryFile fileEntry = (DirectoryFile) workingDirectory.dirTable.get(fileName);
            if (fileEntry.isOpened()) {
                System.out.println("错误：文件 '" + fileName + "' 已打开，请先关闭");
            } else {
                // 释放文件占用的存储空间并从目录表中移除
                fileEntry.file.fileClear(storageDevice);
                workingDirectory.dirTable.remove(fileName);
                System.out.println("文件 '" + fileName + "' 已删除");
            }
        }
    }

    /**
     * 打开文件
     * @param fileName 文件名称
     */
    private void openFile(String fileName) {
        if (fileName.equals(".") || fileName.equals("..") || fileName.contains("/")) {
            // 文件名不能为"."或者"..",并且不能包含"/"
            System.out.println("错误：无效的文件名！");
            System.out.println("文件名不能为\".\"或\"..\",且不能包含\"/\"");
        } else if (!workingDirectory.dirTable.containsKey(fileName)) {
            // 检查文件是否存在
            System.out.println("错误：文件 '" + fileName + "' 不存在");
        } else if (workingDirectory.dirTable.get(fileName).isDirectory) {
            // 检查是否为文件
            System.out.println("错误：'" + fileName + "' 是目录而非文件");
        } else {
            // 打开文件
            DirectoryFile fileEntry = (DirectoryFile) workingDirectory.dirTable.get(fileName);
            if (fileEntry.isOpened()) {
                System.out.println("错误：文件 '" + fileName + "' 已经处于打开状态");
            } else {
                fileEntry.setOpen(true);
                // 注意：这里只是标记文件为打开状态，没有执行实际的内容加载操作
                // 在完整实现中，应该调用fileEntry.file.setOpen(true, storageDevice);
                System.out.println("文件 '" + fileName + "' 已打开");
            }
        }
    }

    /**
     * 关闭文件
     * @param fileName 文件名称
     */
    private void closeFile(String fileName) {
        if (fileName.equals(".") || fileName.equals("..") || fileName.contains("/")) {
            // 文件名不能为"."或者"..",并且不能包含"/"
            System.out.println("错误：无效的文件名！");
            System.out.println("文件名不能为\".\"或\"..\",且不能包含\"/\"");
        } else if (!workingDirectory.dirTable.containsKey(fileName)) {
            // 检查文件是否存在
            System.out.println("错误：文件 '" + fileName + "' 不存在");
        } else if (workingDirectory.dirTable.get(fileName).isDirectory) {
            // 检查是否为文件
            System.out.println("错误：'" + fileName + "' 是目录而非文件");
        } else {
            // 关闭文件
            DirectoryFile fileEntry = (DirectoryFile) workingDirectory.dirTable.get(fileName);
            if (!fileEntry.isOpened()) {
                System.out.println("错误：文件 '" + fileName + "' 未打开");
            } else {
                fileEntry.setOpen(false);
                // 注意：这里只是标记文件为关闭状态，没有执行实际的内容保存操作
                // 在完整实现中，应该调用fileEntry.file.setOpen(false, storageDevice);
                System.out.println("文件 '" + fileName + "' 已关闭");
            }
        }
    }

    /**
     * 读取文件内容
     * @param fileName 文件名称
     * @return 文件内容，如果读取失败则返回null
     */
    private String readFile(String fileName) {
        String buffer = null;
        if (workingDirectory.dirTable.containsKey(fileName) && !workingDirectory.dirTable.get(fileName).isDirectory) {
            // 文件存在时
            System.out.println("Loading...");
            DirectoryFile tmpFile = (DirectoryFile) workingDirectory.dirTable.get(fileName);
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

    /**
     * 写入文件内容
     * @param fileName 文件名称
     * @param buf 要写入的内容
     * @param mode 写入模式 ("a"追加, "w"覆盖)
     */
    private void writeFile(String fileName, String buf, String mode) {
        if (workingDirectory.dirTable.containsKey(fileName) && !workingDirectory.dirTable.get(fileName).isDirectory) {
            // 文件存在时
            System.out.println("Loading...");
            DirectoryFile tmpFile = (DirectoryFile) workingDirectory.dirTable.get(fileName);
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

    /**
     * 运行文件系统命令行界面
     */
    public void run() {
        label:
        while (true) {
            System.out.print("fs> ");
            String command = commandScanner.next();
            String args;
            String buf;
            String rwMode;
            switch (command) {
                case "exit":
                    // 退出文件系统
                    System.out.println("Exiting the VirtualFile System.");
                    persistFileSystem(DiskConst.SerializePath);
                    break label;
                case "mkdir":
                    // 创建目录
                    args = commandScanner.next();
                    mkdir(args);
                    break;
                case "rmdir":
                    // 删除目录
                    args = commandScanner.next();
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
                    args = commandScanner.next();
                    cd(args);
                    break;
                case "create":
                    // 新建文件
                    args = commandScanner.next();
                    createFile(args);
                    break;
                case "delete":
                    // 删除文件
                    args = commandScanner.next();
                    deleteFile(args);
                    break;
                case "open":
                    // 打开文件文件
                    args = commandScanner.next();
                    openFile(args);
                    break;
                case "close":
                    // 关闭文件
                    args = commandScanner.next();
                    closeFile(args);
                    break;
                case "read":
                    // 读取文件
                    args = commandScanner.next();
                    buf = readFile(args);
                    if (buf != null) {
                        System.out.println(args + " contents:\n" + buf);
                    } else {
                        System.out.println("Failed: Read failed. Nothing will be changed.");
                    }
                    break;
                case "write":
                    args = commandScanner.next();
                    buf = commandScanner.next();
                    rwMode = commandScanner.next();
                    writeFile(args, buf, rwMode);
                    break;
                default:
                    System.out.println("Invalid command.");
            }
        }
    }
}