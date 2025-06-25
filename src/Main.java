import java.util.Scanner;

/**
 * 文件系统主程序入口类
 */
public class Main {
    /**
     * 主方法，程序入口点
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);
        String userCommand;
        SimpleFileSystem fsInstance = null;
        
        System.out.println("欢迎使用简易文件系统");
        System.out.println("输入 'new' 创建新文件系统，'sfs' 打开现有文件系统，'exit' 退出程序");

        mainLoop:
        while (true) {
            System.out.print("> ");
            userCommand = inputScanner.next();
            
            switch (userCommand) {
                case "new":
                    System.out.println("创建新的文件系统...");
                    fsInstance = new SimpleFileSystem();
                    fsInstance.run();
                    break;
                    
                case "sfs":
                    System.out.println("打开现有文件系统...");
                    fsInstance = new SimpleFileSystem();
                    fsInstance.run();
                    break;
                    
                case "exit":
                    System.out.println("程序已退出");
                    break mainLoop;
                    
                default:
                    System.out.println("错误：未知命令 '" + userCommand + "'，请输入 'new'、'sfs' 或 'exit'");
                    break;
            }
        }
        
        inputScanner.close();
    }
}