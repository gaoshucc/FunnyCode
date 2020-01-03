layui.use(['element', 'util', 'flow', 'laytpl', 'carousel'], function () {
    let element = layui.element,
        util = layui.util,
        flow = layui.flow,
        laytpl = layui.laytpl,
        carousel = layui.carousel;

    layer.photos({
        photos: '#personal-info',
        anim: 5
    });

    //相关推荐轮播图
    carousel.render({
        elem: '#recommend-carousel',
        width: '100%',
        height: '150px',
        arrow: 'hover',
        indicator: 'inside'
    });

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

    let signature = document.querySelector("#signature");
    signature.addEventListener("blur", function (e) {
        $.ajax({
            type: "POST",
            url: "/user/signature",
            data: {"signature": signature.value},
            dataType: "json",
            success: function (data) {
            },
            async: true
        });
    });

    getAuthorFollowStatus();
    showNotes();
    element.on('tab(publish-infomation)', function(data){
        if(data.index === 0){
            showNotes();
        }else if(data.index === 1){
            showFeed();
        }else{
            console.log("Tab异常");
        }
    });

    //关注用户
    let followUserBtn = document.querySelector("#follow-ta"), isClickFollowBtn = true;
    followUserBtn.addEventListener("click", function (e) {
        if(isClickFollowBtn){
            isClickFollowBtn = false;
            setTimeout(function () {
                isClickFollowBtn = true;
            }, 1000);
            if (hasLogin()) {
                followUser();
            } else {
                showNeedLogin(layer, "小主，要登录才能关注哦 (,,・ω・,,)", getUrlRelativePath());
            }
        }
    });
    /**
     * 关注或取关用户
     * 定义一个 data-tag 属性存储关注状态
     */
    function followUser() {
        let followUserBtn = document.querySelector("#follow-ta"),
            userId = document.querySelector("#user-id").value,
            tag = followUserBtn.getAttribute("data-tag");
        let url;
        if(tag === "-1"){
            url = "/user/follow/user";
        }else if(tag === "1"){
            url = "/user/unfollow/user";
        }else {
            return;
        }

        $.ajax({
            type: "POST",
            url: PRO_NAME + url,
            data: {"userId": userId},
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC){
                    if(data.data != null){
                        let followerCnt = data.data.followerCnt;
                        if(data.data.status === 1){
                            hasfollowUser(followerCnt);
                            layer.msg("关注成功", {
                                time: 2500
                            });
                        }else {
                            unfollowUser();
                            layer.msg("已取消关注", {
                                time: 2500
                            });
                        }
                    }
                }
            },
            async: true
        });
    }

    /**
     * 查询关注状态
     */
    function getAuthorFollowStatus() {
        let userId = document.querySelector("#user-id").value;
        $.ajax({
            type: "GET",
            url: PRO_NAME + "/followstatus/user",
            data: {"userId": userId},
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC){
                    if(data.data != null){
                        let followerCnt = data.data.followerCnt;
                        if(data.data.status === 1){
                            hasfollowUser(followerCnt);
                        }else if(data.data.status === -2){
                            forbidFollowUser();
                        }else {
                            unfollowUser();
                        }
                    }
                }
            },
            async: true
        });
    }
    /**
     * 修改按钮为已关注状态
     * @param followerCnt 关注数
     */
    function hasfollowUser(followerCnt) {
        let followUserBtn = document.querySelector("#follow-ta");
        followUserBtn.innerHTML = "已关注"+" "+followerCnt;
        followUserBtn.setAttribute("data-tag", "1");
        followUserBtn.classList.add("has-follow");
    }
    /**
     * 禁止关注
     */
    function forbidFollowUser(followerCnt) {
        let followUserBtn = document.querySelector("#follow-ta");
        followUserBtn.innerHTML = "关注TA";
        followUserBtn.setAttribute("data-tag", "1");
        followUserBtn.style.cursor = "not-allowed";
    }
    /**
     * 修改按钮为未关注状态
     */
    function unfollowUser(followerCnt) {
        let followUserBtn = document.querySelector("#follow-ta");
        followUserBtn.innerHTML = "关注TA";
        followUserBtn.setAttribute("data-tag", "-1");
        followUserBtn.classList.remove("has-follow");
    }

    function showNotes() {
        let userId = document.querySelector("#user-id").value;
        flow.load({
            elem: '#publish-infomation-note',
            scrollElem: document,
            isAuto: true,
            mb: 150,
            end: "已经到底了 ㄟ(≧◇≦)ㄏ",
            done: function(page, next){
                $.ajax({
                    type: "GET",
                    url: PRO_NAME + "/user/ph/note/" + userId,
                    data: {
                        "page": page,
                        "limit": 6
                    },
                    dataType: "json",
                    success: function (data) {
                        console.log(data);
                        if(data.code === REQ_SUCC && data.data != null && data.data.notes != null && data.data.notes.length > 0){
                            let lis = [];
                            let renderData, template;
                            layui.each(data.data.notes, function(index, item){
                                renderData = { //数据
                                    id: item.id,
                                    title: item.title,
                                    type: item.type,
                                    createTime: util.toDateString(item.createTime, 'yyyy-MM-dd HH:mm:ss'),
                                    readCnt: item.readCnt,
                                    commentCnt: item.commentCnt
                                };
                                template = document.querySelector("#visitor-article-template").innerHTML;
                                laytpl(template).render(renderData, function(html){
                                    lis.push(html);
                                });
                            });

                            next(lis.join(''), page < data.data.pages);
                        }else{
                            let notesBox = document.querySelector("#publish-infomation-note");
                            notesBox.innerHTML = "<div id='no-content-box'><span id='no-content-icon'><svg class='icon' aria-hidden='true'><use xlink:href='#icon-kong'></use></svg></span><span id='no-content'>空空如也，用户暂无手记</span></div>";
                        }
                    },
                    async: true
                });
            }
        });
    }

    function showFeed() {
        let userId = document.querySelector("#user-id").value;
        flow.load({
            elem: '#feeds-block',
            scrollElem: document,
            isAuto: true,
            mb: 150,
            end: "已经到底了 ㄟ(≧◇≦)ㄏ",
            done: function(page, next){
                $.ajax({
                    type: "GET",
                    url: PRO_NAME + "/user/ph/feed/" + userId,
                    data: {
                        "page": page,
                        "limit": 6
                    },
                    dataType: "json",
                    success: function (data) {
                        let lis = [];
                        if(data.code === REQ_SUCC && data.data != null && data.data.feeds != null){
                            let flag, attachment, //flag判断被转发动态是否为空（已被删除），attachment为附带内容
                                forwordFeed, forwordFeedCreateDate, forwordFeedContent; //被转发动态信息
                            layui.each(data.data.feeds, function(index, item){
                                let createTime = util.timeAgo(item.createdDate, false),
                                    map = JSON.parse(item.data),
                                    renderData, template;  //渲染数据，渲染模板
                                if(item.type === 1){
                                    attachment = (map.attachment && map.attachment.length>0) ? JSON.parse(map.attachment) : null;
                                    renderData = { //数据
                                        feed: item,
                                        type: item.type,
                                        createTime: createTime,
                                        attachment: attachment,
                                        attachmentType: item.attachmentType,
                                        map: map,
                                        forwordStateCla: item.forwordState === true ? "has-forword" : "no-forword",
                                        forwordState: item.forwordState === true ? 1 : -1,
                                        likeStateCla: item.likeState === true ? "has-like" : "no-like",
                                        likeState: item.likeState === true ? 1 : -1
                                    };
                                    template = document.querySelector("#original-feed-template").innerHTML;
                                }else{
                                    if(map.feed !== undefined){
                                        flag = true;
                                        forwordFeed = map.feed;
                                        forwordFeedCreateDate = util.timeAgo(map.feed.createdDate, false);
                                        forwordFeedContent = JSON.parse(map.feed.data);
                                        attachment = (forwordFeedContent.attachment && forwordFeedContent.attachment.length>0) ? JSON.parse(forwordFeedContent.attachment) : null;
                                    }else{
                                        flag = false;
                                        forwordFeed = null;
                                        forwordFeedCreateDate = null;
                                        forwordFeedContent = "该条动态已被删除 (ﾒﾟДﾟ)ﾒ";
                                        attachment = null;
                                    }
                                    renderData = { //数据
                                        feed: item,
                                        type: item.type,
                                        createTime: createTime,
                                        content: map.content,
                                        attachmentType: item.attachmentType,

                                        flag: flag,
                                        forword: forwordFeed,
                                        forwordCreateTime: forwordFeedCreateDate,
                                        forwordContent: forwordFeedContent,
                                        forwordAttachment: attachment,
                                        forwordCntId: "forword-cnt-"+item.id,
                                        likeStateCla: item.likeState === true ? "has-like" : "no-like",
                                        likeState: item.likeState === true ? 1 : -1
                                    };
                                    template = document.querySelector("#forword-feed-template").innerHTML;
                                }
                                laytpl(template).render(renderData, function(html){
                                    lis.push(html);
                                });
                            });

                            next(lis.join(''), page < data.data.pages);
                            layer.photos({
                                photos: '.feed-item-content-img-box',
                                anim: 5
                            });
                        }
                        //let noContent = "<div><div id='no-content-box'><span id='no-content-icon'><svg class='icon' aria-hidden='true'><use xlink:href='#icon-kong'></use></svg></span><span id='no-content'>空空如也，用户暂无动态</span></div></div>";
                    },
                    async: true
                });
            }
        });
    }

    let currentUserId = document.querySelector("#current-user").value, content;
    $("#feeds-block").on("click", ".feed-func", function () {
        let ownerId = $(this).attr("data-owner-id"),
            feedId = $(this).attr("data-id");
        if(ownerId === currentUserId){
            content = "<a class='feed-func-point delete-feed' data-id='"+ feedId +"'>删除</a>";
        }else{
            content = "<a class='feed-func-point report-feed' data-id='"+ feedId +"'>举报</a>";
        }
        layer.tips(content, this, {
            id: 'feed-func-point-'+feedId,
            tipsMore: true,
            tips: 2,
            time: 0,
            skin: 'layui-box layui-util-comment-func',
            shade: false,
            success: function (layero, index) {
                $('body').click(function(e) {
                    /**
                     * #feed-func-point-{feedId} : 动态功能tips的id，避免重复弹出相同tips
                     * #feed-func-{feedId} : 动态功能按钮的id，在点击另一评论功能按钮时，可及时关闭其它弹窗
                     */
                    /*!$(e.target).isChildAndSelfOf('#feed-func-point-'+feedId) && */
                    if(!$(e.target).isChildAndSelfOf('#feed-func-'+feedId)){
                        layer.close(index);
                    }
                });
            }
        });
    });

    /**
     * 删除动态
     */
    $(document).on("click", ".delete-feed", function () {
        let feedId = $(this).attr("data-id");
        $.ajax({
            type: "POST",
            url: PRO_NAME + "/user/feed/delete",
            data: {"feedId": feedId},
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC){
                    $("#feed-item-"+feedId).remove();
                    layer.msg("删除动态成功", {
                        time: 2500
                    });
                }else{
                    layer.msg("删除评论失败，请刷新重试", {
                        time: 2500
                    });
                }
            },
            async: true
        });

    });
});