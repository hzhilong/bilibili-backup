<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8">
<title>${part.getPart()}</title>
<style>
body {
    width: calc(100% - 40px);
    height: 100%;
    font-size:16px;
    background-color:#ebffef;
    padding: 10px;
    margin: 10px;
    overflow-x: hidden;
}
table {
    border-collapse: collapse;
    width: 96%;
    table-layout: fixed;
}

table, td, th {
    border: 1px solid black;
}
tr th {
    color:#000000;
    text-align:center;
}
tr td {
    color:#4c4c4c;
    text-align:center;
}
.opt-btn{
    color:#0088ff;
    cursor:pointer;
}
</style>
<script>
</script>
</head>
<body>
  <a class="video-title" href="https://www.bilibili.com/video/${part.getBvid()}?p=${part.getPage()}" target="_blank">
    <h1>${part.getPart()}!</h1>
  </a>
    <div class="c-info">
        <p class="dm-num">弹幕数量：${list?size}</p>
        <p class="re-link">此页面由<a href="https://github.com/hzhilong/bilibili-backup" target="_blank">bilibili-backup</a>生成</p>
        <p >弹幕支持反查用户，不支持16位UID，部分uid不存在对应的用户，可在软件【弹幕文件浏览】快速过滤不存在的用户。</p>
    </div>
    <table cellpadding="2">
      <thead>
        <tr>
          <th style="width: 40px;">序号</th>
          <th style="width: 70px;">视频位置</th>
          <th>弹幕内容</th>
          <th style="width: 170px;">发送时间</th>
          <th style="width: 90px;">权重<br>(智能屏蔽)</th>
          <th style="width: 88px;">用户CRC</th>
          <th style="text-align:left">反查的用户</th>
        </tr>
      </thead>
      <tbody>
        <#list list as dm>
        <tr>
          <td>${dm_index+1}</td>
          <td>${dm.progress}</td>
          <td>${dm.content}</td>
          <td>${dm.time}</td>
          <td>${dm.weight}</td>
          <td>${dm.midHash}</td>
          <td style="text-align:left">
          <#if dm.users??>
            <#list dm.users as dmuser>
              <div class="user-card">
              <span><a href="https://space.bilibili.com/${dmuser.mid?c}" target="_blank">${dmuser.mid?c}</a></span>
              <span>${dmuser.name}</span>
              <span>${dmuser.sex}</span>
              <span>lv${dmuser.level?c}</span>
              <#if dmuser.noFace??&&dmuser.noFace>
              <span>无头像</span>
              </#if>
              </div>
            </#list>
          <#else>
             <#list dm.uids as uid>
                <a href="https://space.bilibili.com/${uid?c}" target="_blank">${uid?c}</a>
             </#list>
          </#if>
          </td>
        </tr>
      </#list>
      </tbody>
    </table>
    <ul>

    </ul>
</body>
</html>