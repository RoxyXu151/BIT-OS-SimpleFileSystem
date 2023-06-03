import java.io.Serializable;

public abstract class DirectoryEntry implements Serializable {
    public String name;                     // 文件名/目录名
    public boolean isDirectory;             // 目录标志
    public Directory parentDir;             // 父目录

    public DirectoryEntry(String name, boolean isDirectory, Directory parentDir) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.parentDir = parentDir;
    }

    @Override
    public String toString() {
        return "DirectoryEntry{" +
                "name='" + name + '\'' +
                ", isDirectory=" + isDirectory +
                ", parentDir=" + parentDir +
                '}';
    }
}
