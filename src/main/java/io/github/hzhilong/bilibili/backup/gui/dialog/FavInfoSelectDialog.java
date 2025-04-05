package io.github.hzhilong.bilibili.backup.gui.dialog;

import io.github.hzhilong.bilibili.backup.api.bean.FavInfo;

import java.awt.*;
import java.util.List;

/**
 * @author hzhilong
 * @version 1.0
 */
public class FavInfoSelectDialog extends ListSelectDialog<FavInfo> {

    public FavInfoSelectDialog(Window parent, String appIconPath, List<FavInfo> list) {
        super(parent, appIconPath, "提示", "请选择要操作的收藏夹：", list,
                new Callback<FavInfo>() {

                    @Override
                    public String[] initColumnNames() {
                        return new String[]{"id", "标题", "视频数"};
                    }

                    @Override
                    public String cellText(FavInfo data, int rowIndex, int columnIndex) {
                        switch (columnIndex) {
                            case 0:
                                return String.valueOf(data.getId());
                            case 1:
                                return data.getTitle();
                            case 2:
                                return String.valueOf(data.getMediaCount());
                        }
                        return "";
                    }
                }, true);
        setSize(600, 400);
        setMinimumSize(new Dimension(600, 400));
    }

}
