package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 收藏夹
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class FavFolder extends FavInfo {


    private String intro;
    private Integer favState;

    private List<Media> medias;

    private boolean saveToDefault;
}
