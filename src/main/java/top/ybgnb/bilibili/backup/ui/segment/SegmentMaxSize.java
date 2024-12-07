package top.ybgnb.bilibili.backup.ui.segment;

/**
 * @ClassName SegmentMaxSize
 * @Description 分段处理的大小
 * @Author hzhilong
 * @Time 2024/12/7
 * @Version 1.0
 */
public enum SegmentMaxSize {
    SIZE_ALL("所有", -1),
    SIZE_50("60", 60),
    SIZE_100("100", 100),
    SIZE_200("200", 200),
    SIZE_500("500", 500);

    private final String text;
    private final int maxSize;

    SegmentMaxSize(String text, int maxSize) {
        this.text = text;
        this.maxSize = maxSize;
    }

    public String getText() {
        return text;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public static SegmentMaxSize parse(String text) {
        for (SegmentMaxSize segmentMaxSize : SegmentMaxSize.values()) {
            if (segmentMaxSize.getText().equals(text)) {
                return segmentMaxSize;
            }
        }
        return SIZE_ALL;
    }

    @Override
    public String toString() {
        return getText();
    }
}
