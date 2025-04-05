package io.github.hzhilong.bilibili.backup.gui.dialog;

import io.github.hzhilong.bilibili.backup.api.bean.FavFolder;

import java.awt.*;
import java.util.List;

/**
 * @author hzhilong
 * @version 1.0
 */
public class FavFolderSelectDialog extends ListSelectDialog<FavFolder> {

    public FavFolderSelectDialog(Window parent, String appIconPath, List<FavFolder> list) {
        this(parent, appIconPath, list, false);
    }

    public FavFolderSelectDialog(Window parent, String appIconPath, List<FavFolder> list, boolean single) {
        super(parent, appIconPath, "提示", "请选择要操作的收藏夹：", list,
                new ListSelectDialog.Callback<FavFolder>() {

                    @Override
                    public String[] initColumnNames() {
                        return new String[]{"序号", "id", "收藏夹标题", "收藏的视频数"};
                    }

                    @Override
                    public String cellText(FavFolder data, int rowIndex, int columnIndex) {
                        switch (columnIndex) {
                            case 0:
                                return String.valueOf(rowIndex + 1);
                            case 1:
                                return String.valueOf(data.getId());
                            case 2:
                                return data.getTitle();
                            case 3:
                                return String.valueOf(data.getMediaCount());
                        }
                        return "";
                    }
                }, single);
    }

}
