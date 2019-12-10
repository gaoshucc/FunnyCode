layui.use(['layer', 'util'], function () {
    let layer = layui.layer,
        util = layui.util;

    //初始化页面时，隐藏Top按钮
    if ($(window).scrollTop() < 100) {
        $("#uptoTop").fadeOut(1);
    }
    $(window).scroll(function () {
        if ($(window).scrollTop() > 100) {
            //当页面起点距离窗口大于100时，淡入
            $("#uptoTop").fadeIn(1000);
        } else {
            //当页面起点距离窗口小于100时，淡出
            $("#uptoTop").fadeOut(1000);
        }
    });
    //跳转到顶部
    let upToTop = document.querySelector("#uptoTop");
    upToTop.addEventListener("click", function (e) {
        $("body,html").animate({scrollTop: 0}, 300);
    });

    findFavoritesCount();
    findFavorites();

    /**
     * 查找收藏夹文章数
     */
    function findFavoritesCount() {
        let favoritesCount = document.querySelector("#favorites-count");
        $.ajax({
            type: "GET",
            url: PRO_NAME + "/user/favorite/count",
            dataType: "json",
            success: function (data) {
                if (data.code === REQ_SUCC){
                    favoritesCount.innerHTML = data.data.count;
                }else{
                    layer.msg(data.code + ":" + data.msg, {
                        time: 2500
                    });
                }
            },
            async: true
        });
    }

    /**
     * 查找我的收藏夹
     */
    function findFavorites() {
        let favoritesSection = document.querySelector("#favorites-section");
        $.ajax({
            type: "GET",
            url: PRO_NAME + "/user/favorite/list",
            data: {"offset":0,"limit":8},
            dataType: "json",
            success: function (data) {
                if (data.code === REQ_SUCC && data.data !== undefined){
                    let notes = data.data.notes;
                    if(notes.length > 0){
                        let favoritesSectionTemp = "";
                        for (let i = 0; i < notes.length; i++) {
                            let createTime = util.toDateString(notes[i].createTime, 'yyyy-MM-dd HH:mm'),
                                collectTime = util.toDateString(notes[i].collectTime, 'yyyy-MM-dd HH:mm');
                            favoritesSectionTemp = favoritesSectionTemp +
                                "<div class='favorites-section-items'>" +
                                "            <article class='article'>" +
                                "                <h2><a class='articleTitle' href='/note/" + notes[i].id + "'>" + notes[i].title + "</a></h2>" +
                                "                <p>点击进入详细阅读</p>" +
                                "                <div class='articleInfo'>" +
                                "                    <span class='content-type'>" + notes[i].type + "</span>" +
                                "                    <time>" + createTime + "</time>" +
                                "                    <div class='readInfo'>" +
                                "                        <span><a>评论</a>" + notes[i].commentCnt + "</span>" +
                                "                    </div>" +
                                "                </div>" +
                                "            </article>" +
                                "            <div class='author-info'>" +
                                "                <div class='author-info-right'>" +
                                "                    <a href='/user/"+ notes[i].userId +"' class='author-nickname'>" + notes[i].nickname + "</a>" +
                                "                    <time class='collect-time'>" + collectTime + "</time>" +
                                "                </div>" +
                                "                <img src='" + notes[i].profile + "'>" +
                                "            </div>" +
                                "        </div>";
                        }
                        favoritesSection.innerHTML = favoritesSectionTemp;
                    }else{
                        favoritesSection.innerHTML = "<span class='icon-zhaobudaojieguo'><svg class='icon' aria-hidden='true'><use xlink:href='#icon-zhaobudaojieguo'></use></svg></span><span id='no-content'>收藏夹里没有内容哦 ( • ̀ω•́ )</span>";
                    }
                }else{
                    layer.msg(data.code + ":" + data.msg, {
                        time: 2500
                    });
                }
            },
            async: true
        });
    }
});