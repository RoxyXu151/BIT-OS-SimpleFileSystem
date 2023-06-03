import java.io.Serializable;

public class DirectoryFile extends DirectoryEntry implements Serializable {
    /*
     * 文件目录项
     * - 文件由文件控制块和文件体组成。
     * - 文件控制块由文件目录项和文件索引节点（inode 节点）组成。
     * - 文件目录项中包含了文件的名称和文件索引节点号（inode 节点号），通过文件文件索引节点号即可访问文件索引节点。
     * - 文件索引节点中包含了文件的其他属性，例如文件大小、文件打开标志、文件索引表。
     * - 文件索引表存储了文件逻辑块号和物理块号的对应关系。
     * - 本实验由 Java 实现，在 Java 中对象为引用类型，类似于 C 语言中的指针，因此不需要在文件目录项目中设置文件索引节点号，
     *   而是直接设置一个文件对象即可。
     */
    public VirtualFile file;                       // 文件

    public DirectoryFile(String name, boolean isDirectory, Directory parentDir) {
        super(name, isDirectory, parentDir);
        this.file = new VirtualFile();
    }

    @Override
    public String toString() {
        return "DirectoryFile{" +
                "virtualFile=" + file +
                ", name='" + name + '\'' +
                ", isDirectory=" + isDirectory +
                ", parentDir=" + parentDir +
                '}';
    }
}
