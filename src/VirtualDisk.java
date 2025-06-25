import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 存储设备类
 * 模拟物理存储设备，管理存储块的分配和释放
 */
public class VirtualDisk implements Serializable {
    // 存储设备标识符
    public String deviceId;                 
    // 存储块数组
    public VirtualBlock[] storageBlocks;    
    // 存储块分配状态表 (0=空闲, 1=已分配)
    public int[] allocationMap;             
    // 存储设备空间状态
    private boolean storageExhausted;       

    /**
     * 创建新的存储设备实例
     * @param deviceId 存储设备标识符
     */
    public VirtualDisk(String deviceId) {
        this.deviceId = deviceId;
        this.storageBlocks = new VirtualBlock[DiskConst.DISK_SIZE];
        this.allocationMap = new int[DiskConst.DISK_SIZE];
        this.storageExhausted = false;
        
        // 初始化所有存储块
        for (int i = 0; i < DiskConst.DISK_SIZE; i++) {
            this.storageBlocks[i] = new VirtualBlock(i);
            this.allocationMap[i] = 0;
        }
    }

    /**
     * 分配指定数量的存储块
     * @param blockCount 需要分配的存储块数量
     * @return 分配的存储块索引列表
     */
    public ArrayList<Integer> diskAlloc(int blockCount) {
        ArrayList<Integer> allocatedBlocks = new ArrayList<>();
        for (int i = 0; i < blockCount; i++) {
            for (int j = 0; j < DiskConst.DISK_SIZE; j++) {
                if (allocationMap[j] == 0) {
                    allocatedBlocks.add(j);
                    allocationMap[j] = 1;
                    break;
                }
            }
        }
        return allocatedBlocks;
    }

    /**
     * 释放指定的存储块
     * @param blockIndices 要释放的存储块索引列表
     */
    public void diskFree(ArrayList<Integer> blockIndices) {
        for (Integer index : blockIndices) {
            storageBlocks[index].clearBlock();
            allocationMap[index] = 0;
        }
    }

    /**
     * 显示存储设备使用情况
     */
    public void diskUsage() {
        if (isFull()) {
            System.out.println("Storage device is completely full.");
        } else {
            int usedCount = 0;
            for (int i = 0; i < DiskConst.DISK_SIZE; i++) {
                usedCount += allocationMap[i];
            }
            int usagePercentage = (int) (100.0 * usedCount / DiskConst.DISK_SIZE);
            System.out.println("Storage usage: " + usagePercentage + "%");
            System.out.println("Allocated blocks: " + usedCount);
            System.out.println("Available blocks: " + (DiskConst.DISK_SIZE - usedCount));
        }
    }

    /**
     * 检查存储设备是否已满
     * @return 如果存储设备已满则返回true，否则返回false
     */
    public boolean isFull() {
        storageExhausted = true;
        for (int i = 0; i < DiskConst.DISK_SIZE; i++) {
            if (!storageBlocks[i].isUSED()) {
                storageExhausted = false;
                break;
            }
        }
        return storageExhausted;
    }

    @Override
    public String toString() {
        return "VirtualDisk{" +
                "deviceId='" + deviceId + '\'' +
                ", storageBlocks=" + Arrays.toString(storageBlocks) +
                ", allocationMap=" + Arrays.toString(allocationMap) +
                ", storageExhausted=" + storageExhausted +
                '}';
    }
}