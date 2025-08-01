import java.io.Serializable;

/**
 * 文件系统文档类
 * 表示文件系统中的文件，管理文件的元数据和内容
 */
public class FileSystemDocument extends FileSystemNode implements Serializable {
    /**
     * 文件内容管理器
     * 负责文件实际内容的存储和索引管理
     * 
     * 在此实现中，我们使用Java对象引用直接关联到文件内容管理器，
     * 而不是使用传统文件系统中的inode号码间接引用
     */
    public VirtualFileContent file;

    /**
     * 创建一个新的文件文档
     * 
     * @param name 文件名称
     * @param isDirectory 是否为目录（应始终为false）
     * @param parentDir 父目录引用
     */
    public FileSystemDocument(String name, boolean isDirectory, FileSystemFolder parentDir) {
        super(name, isDirectory, parentDir);
        this.file = new VirtualFileContent();
    }

    /**
     * 检查文件是否处于打开状态
     * @return 如果文件已打开则返回true，否则返回false
     */
    public boolean isOpened() {
        return file.isOpen();
    }
    
    /**
     * 设置文件的打开或关闭状态
     * 注意：此方法是一个简化版本，仅用于标记文件的打开状态
     * 完整的文件打开/关闭操作应该调用VirtualFileContent.setOpen(boolean, StorageDevice)
     * 
     * @param status 目标状态，true表示打开，false表示关闭
     */
    public void setOpen(boolean status) {
        // 使用VirtualFileContent提供的方法设置状态
        file.setOpenStatus(status);
    }

    @Override
    public String toString() {
        return "FileSystemDocument{" +
                "name='" + name + '\'' +
                "file=" + file +
                ", isDirectory=" + isDirectory +
                ", parentDir=" + parentDir +
                '}';
    }
}
