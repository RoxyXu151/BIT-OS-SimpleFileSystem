import java.io.Serializable;
import java.util.HashMap;

public class Directory extends DirectoryEntry implements Serializable {
    public HashMap<String, DirectoryEntry> dirTable;        // 目录表

    public Directory(String name, boolean isDirectory, Directory parentDir) {
        super(name, isDirectory, parentDir);
        dirTable = new HashMap<>();
    }

    @Override
    public String toString() {
        return "Directory{" +
                "dirTable=" + dirTable +
                ", name='" + name + '\'' +
                ", isDirectory=" + isDirectory +
                ", parentDir=" + parentDir +
                '}';
    }
}