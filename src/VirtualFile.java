import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class VirtualFile implements Serializable {
    private final HashMap<Integer, Integer> indexTable;       // 文件索引表
    private int fileSize;                               // 文件大小
    private boolean isOpen;                             // 文件打开标志
    private String buffer;                              // 文件缓冲区

    public VirtualFile() {
        this.indexTable = new HashMap<>();
        this.fileSize = 0;
        this.isOpen = false;
        this.buffer = "";
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open, VirtualDisk vDisk) {
        // 打开文件时，将文件从磁盘加载到文件缓冲区；
        // 关闭文件时，将文件从文件缓冲区写入到磁盘；
        isOpen = open;
        if (isOpen) {
            // 清空文件缓冲区
            buffer = "";
            // 计算文件虚拟块数
            int blockNum = (int) Math.ceil(1.0 * fileSize / DiskConst.BLOCK_SIZE);
            // 将文件从磁盘加载到文件缓冲区
            for (int i = 0; i < blockNum; i++) {
                int idx = indexTable.get(i);
                buffer += vDisk.diskBlocks[idx].readBlock();
            }
        } else {
            // 计算文件大小
            fileSize = buffer.length();
            // 计算文件虚拟块数
            int blockNum = (int) Math.ceil(1.0 * fileSize / DiskConst.BLOCK_SIZE);
            // 从磁盘中申请空间
            ArrayList<Integer> blockBuf = vDisk.diskAlloc(blockNum);
            // 将文件按照虚拟块大小进行拆分
            ArrayList<String> contentBuf = new ArrayList<>();
            for (int i = 0; i < fileSize; i += DiskConst.BLOCK_SIZE) {
                contentBuf.add(buffer.substring(i, Math.min(buffer.length(), i + DiskConst.BLOCK_SIZE)));
            }
            // 更新文件索引表并将文件内容写入到磁盘中
            for (int i = 0; i < blockNum; i++) {
                int idx = blockBuf.get(i);
                // 记录索引
                indexTable.put(i, idx);
                // 写入磁盘
                vDisk.diskBlocks[idx].writeBlock(contentBuf.get(i));
            }
        }
    }

    public String getContent() {
        return buffer;
    }

    public void setContent(String content, String mode) {
        if (mode.equals("a")) {
            // 追加模式
            buffer += content;
        } else if (mode.equals("w")) {
            // 覆盖模式
            buffer = content;
        }
    }

    public boolean fileClear(VirtualDisk vDisk) {
        boolean isOK = false;
        if (!isOpen) {
            ArrayList<Integer> indexList = new ArrayList<>(indexTable.values());
            vDisk.diskFree(indexList);
            isOK = true;
        } else {
            System.out.println("Failed: This virtualFile is still open, please close it first.");
        }
        return isOK;
    }

    @Override
    public String toString() {
        return "VirtualFile{" +
                "indexTable=" + indexTable +
                ", fileSize=" + fileSize +
                ", isOpen=" + isOpen +
                ", buffer='" + buffer + '\'' +
                '}';
    }
}
