package io.github.hzhilong.bilibili.backup.gui.dialog;

import io.github.hzhilong.bilibili.backup.api.bean.RelationTag;

import java.awt.*;
import java.util.List;

/**
 * @author hzhilong
 * @version 1.0
 */
public class RelationTagSelectDialog extends ListSelectDialog<RelationTag> {

    public RelationTagSelectDialog(Window parent, String appIconPath, List<RelationTag> list) {
        super(parent, appIconPath, "提示", "请选择要操作的关注分组：", list,
                new Callback<RelationTag>() {

                    @Override
                    public String[] initColumnNames() {
                        return new String[]{"序号", "关注分组", "关注人数"};
                    }

                    @Override
                    public String cellText(RelationTag data, int rowIndex, int columnIndex) {
                        switch (columnIndex) {
                            case 0:
                                return String.valueOf(rowIndex + 1);
                            case 1:
                                return data.getName();
                            case 2:
                                return String.valueOf(data.getCount());
                        }
                        return "";
                    }
                });
    }

}
