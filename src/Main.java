import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        SimpleFileSystem fileSystem = null;
        Scanner in = new Scanner(System.in);
        label:
        while (true) {
            System.out.print("> ");
            String command = in.next();
            switch (command) {
                // 创建文件系统
                case "new" -> {
                    if (fileSystem == null) {
                        System.out.println("VirtualFile System is created.");
                        fileSystem = new SimpleFileSystem();
                    } else {
                        System.out.println("VirtualFile System already exists.");
                    }
                }
                // 启动文件系统
                case "sfs" -> {
                    if (fileSystem != null) {
                        System.out.println("VirtualFile System is running.");
                        fileSystem.run();
                    } else {
                        System.out.println("VirtualFile System is not existed. You can use \"new\" to create one!");
                    }
                }
                // 退出终端
                case "exit" -> {
                    System.out.println("Exiting the shell.");
                    in.close();
                    break label;
                }
                default -> System.out.println("Invalid command.");
            }
        }
    }
}