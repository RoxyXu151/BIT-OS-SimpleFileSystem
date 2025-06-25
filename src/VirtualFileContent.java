import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 虚拟文件内容管理类
 * 负责文件内容的存储、读取和管理，实现了文件索引分配机制
 */
public class VirtualFileContent implements Serializable {
    // 文件逻辑块到物理块的映射表
    private final HashMap<Integer, Integer> blockMapping;    
    // 文件当前大小（字节数）
    private int contentSize;                                
    // 文件是否处于打开状态
    private boolean openStatus;                             
    // 文件内容缓存
    private String contentCache;                            

    /**
     * 创建一个新的空文件内容管理器
     */
    public VirtualFileContent() {
        this.blockMapping = new HashMap<>();
        this.contentSize = 0;
        this.openStatus = false;
        this.contentCache = "";
    }

    /**
     * 检查文件是否处于打开状态
     * @return 如果文件已打开则返回true，否则返回false
     */
    public boolean isOpen() {
        return openStatus;
    }
    
    /**
     * 设置文件的打开状态（仅用于标记，不涉及实际的内容加载/保存）
     * 注意：此方法仅用于简化实现，完整操作应使用setOpen(boolean, StorageDevice)
     * 
     * @param status 目标状态，true表示打开，false表示关闭
     */
    public void setOpenStatus(boolean status) {
        this.openStatus = status;
    }

    /**
     * 计算存储指定大小内容所需的存储块数量
     * @param size 内容大小（字节数）
     * @return 所需的存储块数量
     */
    private int calculateRequiredBlocks(int size) {
        return (int) Math.ceil(1.0 * size / StorageConstants.BLOCK_SIZE);
    }

    /**
     * 将内容按存储块大小分割成片段
     * @param content 要分割的内容
     * @return 分割后的内容片段列表
     */
    private ArrayList<String> fragmentContent(String content) {
        ArrayList<String> fragments = new ArrayList<>();
        for (int i = 0; i < content.length(); i += StorageConstants.BLOCK_SIZE) {
            fragments.add(content.substring(i, Math.min(content.length(), i + StorageConstants.BLOCK_SIZE)));
        }
        return fragments;
    }

    /**
     * 打开或关闭文件
     * 打开时从存储设备加载内容到缓存，关闭时将缓存写回存储设备
     * 
     * @param status 目标状态，true表示打开，false表示关闭
     * @param storage 存储设备实例
     */
    public void setOpen(boolean status, StorageDevice storage) {
        openStatus = status;
        
        if (openStatus) {
            // 打开文件：从存储设备加载内容到缓存
            contentCache = "";
            int requiredBlocks = calculateRequiredBlocks(contentSize);
            // 从存储设备读取内容到缓存
            for (int i = 0; i < requiredBlocks; i++) {
                int blockIndex = blockMapping.get(i);
                contentCache += storage.storageBlocks[blockIndex].readBlock();
            }
        } else {
            // 关闭文件：将缓存内容写回存储设备
            contentSize = contentCache.length();
            int requiredBlocks = calculateRequiredBlocks(contentSize);
            // 分配存储空间
            ArrayList<Integer> allocatedBlocks = storage.diskAlloc(requiredBlocks);
            // 将内容分割成适合存储块大小的片段
            ArrayList<String> contentFragments = fragmentContent(contentCache);
            // 更新映射表并写入存储设备
            for (int i = 0; i < requiredBlocks; i++) {
                int blockIndex = allocatedBlocks.get(i);
                blockMapping.put(i, blockIndex);
                storage.storageBlocks[blockIndex].writeBlock(contentFragments.get(i));
            }
        }
    }

    /**
     * 获取文件内容
     * @return 文件的完整内容字符串
     */
    public String getContent() {
        return contentCache;
    }

    /**
     * 设置文件内容
     * @param content 要写入的内容
     * @param mode 写入模式："a"表示追加，"w"表示覆盖
     */
    public void setContent(String content, String mode) {
        if (mode.equals("a")) {
            // 追加模式
            contentCache += content;
        } else if (mode.equals("w")) {
            // 覆盖模式
            contentCache = content;
        }
    }

    /**
     * 清空文件内容并释放占用的存储块
     * @param storage 存储设备引用
     * @return 操作是否成功
     */
    public boolean fileClear(StorageDevice storage) {
        boolean success = false;
        if (!openStatus) {
            ArrayList<Integer> blocksToFree = new ArrayList<>(blockMapping.values());
            storage.diskFree(blocksToFree);
            success = true;
        } else {
            System.out.println("错误：文件当前处于打开状态，请在删除前关闭文件。");
        }
        return success;
    }

    @Override
    public String toString() {
        return "VirtualFileContent{" +
                "blockMapping=" + blockMapping +
                ", contentSize=" + contentSize +
                ", openStatus=" + openStatus +
                ", contentCache='" + contentCache + '\'' +
                '}';
    }
}
