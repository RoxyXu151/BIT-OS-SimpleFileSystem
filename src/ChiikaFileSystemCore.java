import java.io.*;
import java.util.Scanner;

/**
 * Chiika文件系统核心实现类
 * 提供文件和目录管理的核心功能
 */
public class ChiikaFileSystemCore {

    // 文件系统根目录
    private static FileSystemFolder rootDirectory = new FileSystemFolder("/", true, null); 
    // 用户输入处理器
    private final Scanner commandScanner;           
    // 当前工作目录
    private FileSystemFolder workingDirectory;       
    // 当前工作路径
    private String workingPath;         
    // 存储设备实例
    private StorageDevice storageDevice;          

    /**
     * 创建新的文件系统实例
     * 如果存在持久化数据，则加载现有文件系统状态
     */
    public ChiikaFileSystemCore() {
        this.workingDirectory = rootDirectory;
        this.workingPath = "/";
        this.storageDevice = new StorageDevice("/dev/sda");
        this.commandScanner = new Scanner(System.in);
        
        // 检查是否存在持久化的文件系统数据
        File persistenceFile = new File(StorageConstants.SERIALIZE_PATH);
        if (persistenceFile.exists()) {
            restoreFileSystem(StorageConstants.SERIALIZE_PATH);
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
            this.storageDevice = (StorageDevice) objectIn.readObject();
            rootDirectory = (FileSystemFolder) objectIn.readObject();
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
     * 验证文件或目录名是否合法
     * @param name 文件或目录名
     * @return 如果名称合法返回true，否则返回false
     */
    private boolean isValidName(String name) {
        if (name.equals(".") || name.equals("..") || name.contains("/")) {
            System.out.println("错误：无效的名称！");
            System.out.println("名称不能为\".\"或\"..\",且不能包含\"/\"");
            return false;
        }
        return true;
    }

    /**
     * 创建新目录
     * @param directoryName 目录名称
     */
    public void mkdir(String directoryName) {
        if (!isValidName(directoryName)) {
            return;
        }
        if (workingDirectory.containsEntry(directoryName)) {
            System.out.println("错误：目录 '" + directoryName + "' 已存在");
        } else {
            workingDirectory.addEntry(directoryName, new FileSystemFolder(directoryName, true, workingDirectory));
            System.out.println("目录 '" + directoryName + "' 已创建");
        }
    }

    /**
     * 删除目录
     * @param directoryName 目录名称
     */
    public void rmdir(String directoryName) {
        if (!workingDirectory.containsEntry(directoryName) || !workingDirectory.getEntry(directoryName).isDirectory) {
            System.out.println("Failed: " + directoryName + " is not existed.");
            return;
        }
        
        FileSystemFolder tmpDir = (FileSystemFolder) workingDirectory.getEntry(directoryName);
        if (tmpDir.isEmpty()) {
            workingDirectory.removeEntry(directoryName);
            System.out.println(directoryName + " is removed.");
        } else {
            System.out.println("""
                    Failed: Current directory is not empty.
                    Do you want to remove all files and folders in this folder?
                    Enter "Y" or "y" to continue, otherwise nothing will be changed.""");
            String answer = commandScanner.next();
            if (answer.equals("Y") || answer.equals("y")) {
                removeDirectoryRecursively(directoryName);
                System.out.println(directoryName + " is removed.");
            } else {
                System.out.println("Nothing will be changed.");
            }
        }
    }

    /**
     * 递归删除目录及其所有内容
     * @param directoryName 目录名称
     */
    private void removeDirectoryRecursively(String directoryName) {
        FileSystemFolder stashCurrDir = workingDirectory;
        String stashCurrentPath = workingPath;
        
        if (!workingDirectory.containsEntry(directoryName) || !workingDirectory.getEntry(directoryName).isDirectory) {
            return;
        }
        
        FileSystemFolder tmpDir = (FileSystemFolder) workingDirectory.getEntry(directoryName);
        if (tmpDir.isEmpty()) {
            workingDirectory.removeEntry(directoryName);
        } else {
            cd("." + "/" + directoryName);
            for (String fName : workingDirectory.getEntryNames()) {
                if (workingDirectory.getEntry(fName).isDirectory) {
                    removeDirectoryRecursively(fName);
                } else {
                    deleteFile(fName);
                }
            }
            cd("./..");
            workingDirectory.removeEntry(directoryName);
        }
        
        workingDirectory = stashCurrDir;
        workingPath = stashCurrentPath;
    }

    /**
     * 显示当前目录下所有目录项
     */
    public void ls() {
        pwd();
        // 目录为紫色；文件为浅蓝色。
        for (String name : workingDirectory.getEntryNames()) {
            if (workingDirectory.getEntry(name).isDirectory) {
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
        FileSystemFolder tmpCurrDir = null;                        // 临时Dir变量
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
                    if (tmpCurrDir.containsEntry(dirName) && tmpCurrDir.getEntry(dirName).isDirectory) {
                        // 访问下一层
                        tmpCurrDir = (FileSystemFolder) tmpCurrDir.getEntry(dirName);
                        tmpPath.append(dirName).append("/");
                    } else if (dirName.equals("..") && tmpCurrDir.getParentDir() != null) {
                        // 访问上一层
                        tmpCurrDir = tmpCurrDir.getParentDir();
                        tmpPath = new StringBuilder(tmpPath.substring(0, tmpPath.length() - workingDirectory.getName().length() - 1));
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
     * 验证文件是否存在且为有效文件
     * @param fileName 文件名
     * @return 如果文件有效返回FileSystemDocument对象，否则返回null
     */
    private FileSystemDocument validateFile(String fileName) {
        if (!isValidName(fileName)) {
            return null;
        }
        if (!workingDirectory.containsEntry(fileName)) {
            System.out.println("错误：文件 '" + fileName + "' 不存在");
            return null;
        }
        if (workingDirectory.getEntry(fileName).isDirectory) {
            System.out.println("错误：'" + fileName + "' 是目录而非文件");
            return null;
        }
        return (FileSystemDocument) workingDirectory.getEntry(fileName);
    }

    /**
     * 创建新文件
     * @param fileName 文件名称
     */
    private void createFile(String fileName) {
        if (!isValidName(fileName)) {
            return;
        }
        if (workingDirectory.containsEntry(fileName)) {
            System.out.println("Failed: " + fileName + " is already existed.");
        } else {
            workingDirectory.addEntry(fileName, new FileSystemDocument(fileName, false, workingDirectory));
            System.out.println(fileName + " is created.");
        }
    }

    /**
     * 删除文件
     * @param fileName 文件名称
     */
    private void deleteFile(String fileName) {
        FileSystemDocument fileEntry = validateFile(fileName);
        if (fileEntry == null) {
            return;
        }
        if (fileEntry.isOpened()) {
            System.out.println("错误：文件 '" + fileName + "' 已打开，请先关闭");
        } else {
            fileEntry.file.fileClear(storageDevice);
            workingDirectory.removeEntry(fileName);
            System.out.println("文件 '" + fileName + "' 已删除");
        }
    }

    /**
     * 打开文件
     * @param fileName 文件名称
     */
    private void openFile(String fileName) {
        FileSystemDocument fileEntry = validateFile(fileName);
        if (fileEntry == null) {
            return;
        }
        if (fileEntry.isOpened()) {
            System.out.println("错误：文件 '" + fileName + "' 已经处于打开状态");
        } else {
            fileEntry.setOpen(true);
            System.out.println("文件 '" + fileName + "' 已打开");
        }
    }

    /**
     * 关闭文件
     * @param fileName 文件名称
     */
    private void closeFile(String fileName) {
        FileSystemDocument fileEntry = validateFile(fileName);
        if (fileEntry == null) {
            return;
        }
        if (!fileEntry.isOpened()) {
            System.out.println("错误：文件 '" + fileName + "' 未打开");
        } else {
            fileEntry.setOpen(false);
            System.out.println("文件 '" + fileName + "' 已关闭");
        }
    }

    /**
     * 读取文件内容
     * @param fileName 文件名称
     * @return 文件内容，如果读取失败则返回null
     */
    private String readFile(String fileName) {
        FileSystemDocument tmpFile = validateFile(fileName);
        if (tmpFile == null) {
            return null;
        }
        
        System.out.println("Loading...");
        if (tmpFile.file.isOpen()) {
            return tmpFile.file.getContent();
        } else {
            System.out.println("Failed: " + fileName + " is not opened.");
            return null;
        }
    }

    /**
     * 写入文件内容
     * @param fileName 文件名称
     * @param buf 要写入的内容
     * @param mode 写入模式 ("a"追加, "w"覆盖)
     */
    private void writeFile(String fileName, String buf, String mode) {
        FileSystemDocument tmpFile = validateFile(fileName);
        if (tmpFile == null) {
            return;
        }
        
        System.out.println("Loading...");
        if (!tmpFile.file.isOpen()) {
            System.out.println("Failed: " + fileName + " is not opened.");
        } else if (!mode.equals("a") && !mode.equals("w")) {
            System.out.println("Failed: Invalid mode " + mode + ".");
        } else {
            tmpFile.file.setContent(buf, mode);
            System.out.println(fileName + " has been written.");
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
                    persistFileSystem(StorageConstants.SERIALIZE_PATH);
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