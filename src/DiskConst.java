public class DiskConst {
    /**
     * 磁盘常量
     *      常量名               单位                    描述
     *      DISK_SIZE           VirtualBlock            磁盘容量
     *      SYSTEM_SIZE         VirtualBlock            系统分区
     *      USER_SIZE           VirtualBlock            用户分区
     *      TABLE_SIZE          VirtualBlock            组表容量
     *      BLOCK_SIZE          Character               虚拟块容量
     * */
    public static int DISK_SIZE = 12000;
    public static int BLOCK_SIZE = 100;
    public static String SerializePath = "fileSystem.txt";
}
