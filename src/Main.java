import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ChiikaFileSystemCore fs = null;
        System.out.println("欢迎使用简易文件系统！");
        System.out.println("请选择操作：");
        System.out.println("new: 创建新的文件系统");
        System.out.println("sfs: 打开已有的文件系统");
        System.out.println("exit: 退出");
        System.out.print("请输入:");
        String choice = scanner.next();
        switch (choice) {
            case "new":
                fs = new ChiikaFileSystemCore();
                System.out.println("已创建新的文件系统");
                break;
            case "sfs":
                fs = new ChiikaFileSystemCore();
                System.out.println("已打开现有文件系统");
                break;
            case "exit":
                System.out.println("正在退出...");
                System.exit(0);
                break;
            default:
                System.out.println("无效选项，正在退出...");
                System.exit(1);
        }
        fs.run();
    }
}