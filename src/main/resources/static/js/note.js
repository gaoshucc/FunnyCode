layui.use(['layer','carousel','util'], function () {
    //layui模块
    let layer = layui.layer,
        carousel = layui.carousel,
        util = layui.util;

    main();
    getAuthorFollowStatus();
    getCollectStatus();
    getLikeStatus();
    findLikeCount();
    findDislikeCount();
    getAuthorNoteCnt();
    findComments();

    function main() {
        //相关推荐轮播图
        carousel.render({
            elem: '#recommend-carousel',
            width: '100%',
            height: '150px',
            arrow: 'hover',
            indicator: 'inside'
        });

        if ($(window).scrollTop() < 100) {
            $("#uptoTop").fadeOut(1);   //初始化页面时，隐藏Top按钮
        }

        $(window).scroll(function () {
            if ($(window).scrollTop() > 100) {
                $("#uptoTop").fadeIn(1000);  //当页面起点距离窗口大于100时，淡入
            } else {
                $("#uptoTop").fadeOut(1000);    //当页面起点距离窗口小于100时，淡出
            }
        });

        //举报
        let report = document.querySelector("#report"), isClickReportBtn = true;
        report.addEventListener("click", function (e) {
            if(isClickReportBtn){
                isClickReportBtn = false;
                setTimeout(function () {
                    isClickReportBtn = true;
                }, 1000);
                if(hasLogin()){
                    let noteId = document.querySelector("#noteId");
                    let title = document.querySelector("#title");
                    layer.open({
                        type: 2,
                        title: '举报手记【'+title.innerHTML+'】',
                        shadeClose: true,
                        shade: 0.3,
                        area: ['420px', '300px'],
                        content: '/user/report/' + ENTITY_NOTE + "/" + noteId.value,
                        end: function () {}
                    });
                }else{
                    showNeedLogin(layer, "小主，要登录才能举报哦 (,,・ω・,,)", getUrlRelativePath());
                }
            }
        })
        //跳转到顶部
        let uptoTop = document.querySelector("#uptoTop");
        uptoTop.addEventListener("click", function (e) {
            $("body,html").animate({scrollTop: 0}, 300);
        })
        //收藏
        let favorite = document.querySelector("#favorite");
        favorite.addEventListener("click", function (e) {
            if (hasLogin()) {
                collectNote();
            } else {
                showNeedLogin(layer, "小主，要登录才能收藏哦 (,,・ω・,,)", getUrlRelativePath());
            }
        });
        //点赞
        let like = document.querySelector("#like"), isClickNoteLikeBtn = true;
        like.addEventListener("click", function (e) {
            if(isClickNoteLikeBtn){
                isClickNoteLikeBtn = false;
                setTimeout(function () {
                    isClickNoteLikeBtn = true;
                }, 1000);
                if (hasLogin()) {
                    likeNote();
                } else {
                    showNeedLogin(layer, "小主，要登录才能点赞哦 (,,・ω・,,)", getUrlRelativePath());
                }
            }
        });
        //点踩
        let dislike = document.querySelector("#dislike"), isClickNoteDislikeBtn = true;
        dislike.addEventListener("click", function (e) {
            if(isClickNoteDislikeBtn){
                isClickNoteDislikeBtn = false;
                setTimeout(function () {
                    isClickNoteDislikeBtn = true;
                }, 1000);
                if (hasLogin()) {
                    dislikeNote();
                } else {
                    showNeedLogin(layer, "小主，要登录才能点踩哦 (,,・ω・,,)", getUrlRelativePath());
                }
            }
        });
        //关注用户
        let followUserBtn = document.querySelector("#follow-user"), isClickFollowBtn = true;
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
        //渲染评论编辑区
        let writeCommentBox = document.querySelector("#write-comment-box");
        if(hasLogin()){
            writeCommentBox.innerHTML = "<div id='can-comment'>" +
                "<textarea id='comment-content' name='content' style='visibility:hidden;'></textarea>" +
                "<a id='submit-comment'>发表评论</a>" +
                "</div>";
            //KindEditor编辑器
            let kindEditor = KindEditor.create('#comment-content', {
                width: '100%',       // 文本框宽度(可以百分比或像素)
                height: '100px',     // 文本框高度(只能像素)
                minWidth: 200,       // 最小宽度（数字）
                minHeight: 100,      // 最小高度（数字）
                themeType: 'simple',
                resizeType: 0,
                items: ['emoticons', 'link']
            });
            //发表评论
            let noteId = document.querySelector("#noteId");
            let commentContent = document.querySelector("#comment-content");
            let submitComment = document.querySelector("#submit-comment"), isClickSubmitCommentBtn = true;
            submitComment.addEventListener("click", function (e) {
                if(isClickSubmitCommentBtn){
                    isClickSubmitCommentBtn = false;
                    setTimeout(function () {
                        isClickSubmitCommentBtn = true;
                    }, 1500);
                    kindEditor.sync();
                    addComment(kindEditor, noteId.value, commentContent.value, 0);
                }
            });
        }else{
            writeCommentBox.innerHTML = "<div id='forbid-comment'>" +
                "<span><a href='/relogin?next="+ getUrlRelativePath() +"' title='前往登录'>登录</a>后可评论</span>" +
                "</div>";
        }
    }

    /**
     * 查询关注状态
     */
    function getAuthorFollowStatus() {
        let writerInfo = document.querySelector("#writerInfo");
        $.ajax({
            type: "GET",
            url: PRO_NAME + "/followstatus/user",
            data: {"userId": writerInfo.getAttribute("data-id")},
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC){
                    if(data.data !== undefined){
                        let fansCount = document.querySelector("#fansCount");
                        let followerCnt = data.data.followerCnt;
                        fansCount.innerHTML = followerCnt;
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
     * 关注或取关用户
     * 定义一个 data-tag 属性存储关注状态
     */
    function followUser() {
        let followUserBtn = document.querySelector("#follow-user");
        let writerInfo = document.querySelector("#writerInfo");
        let tag = followUserBtn.getAttribute("data-tag");
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
            data: {"userId": writerInfo.getAttribute("data-id")},
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC){
                    if(data.data !== undefined){
                        let fansCount = document.querySelector("#fansCount");
                        let followerCnt = data.data.followerCnt;
                        fansCount.innerHTML = followerCnt;
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
     * 修改按钮为已关注状态
     * @param followerCnt 关注数
     */
    function hasfollowUser(followerCnt) {
        let followUserBtn = document.querySelector("#follow-user");
        followUserBtn.innerHTML = "已关注"+" "+followerCnt;
        followUserBtn.setAttribute("data-tag", "1");
        followUserBtn.classList.add("has-follow");
    }
    /**
     * 禁止关注
     */
    function forbidFollowUser() {
        let followUserBtn = document.querySelector("#follow-user");
        followUserBtn.innerHTML = "关注TA";
        followUserBtn.setAttribute("data-tag", "1");
        followUserBtn.style.cursor = "not-allowed";
    }
    /**
     * 修改按钮为未关注状态
     */
    function unfollowUser() {
        let followUserBtn = document.querySelector("#follow-user");
        followUserBtn.innerHTML = "关注TA";
        followUserBtn.setAttribute("data-tag", "-1");
        followUserBtn.classList.remove("has-follow");
    }

    /**
     * 获取作者原创手记数
     */
    function getAuthorNoteCnt() {
        let noteCount = document.querySelector("#noteCount");
        let writerInfo = document.querySelector("#writerInfo");
        $.ajax({
            type: "GET",
            url: PRO_NAME + "/note/count",
            data: {"userId": writerInfo.getAttribute("data-id")},
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC){
                    if(data.data !== undefined){
                        noteCount.innerHTML = data.data.notecnt;
                    }
                }
            },
            async: true
        });
    }
    /**
     * 获得点赞数
     */
    function findLikeCount() {
        let noteId = document.querySelector("#noteId");
        let likeCount = document.querySelector("#like span");
        $.ajax({
            type: "GET",
            url: PRO_NAME + "/note/likecnt",
            data: {"noteId": noteId.value},
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC){
                    if(data.data != null){
                        likeCount.innerHTML = data.data.likeCnt;
                    }
                }
            },
            async: true
        });
    }
    /**
     * 获得点踩数
     */
    function findDislikeCount() {
        let noteId = document.querySelector("#noteId");
        let dislikeCount = document.querySelector("#dislike span");
        $.ajax({
            type: "GET",
            url: PRO_NAME + "/note/dislikecnt",
            data: {"noteId": noteId.value},
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC){
                    if(data.data !== undefined){
                        dislikeCount.innerHTML = data.data.dislikeCnt;
                    }
                }
            },
            async: true
        });
    }

    /**
     * 是否已点赞
     */
    function getLikeStatus() {
        let noteId = document.querySelector("#noteId");
        $.ajax({
            type: "GET",
            url: PRO_NAME + "/note/likestatus",
            data: {"noteId": noteId.value},
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC){
                    if(data.data !== undefined){
                        let status = data.data.status;
                        if (status === 1) {
                            like();
                        } else if(status === -1){
                            dislike();
                        }
                    }
                }
            },
            async: true
        });
    }
    /**
     * 点赞
     */
    function likeNote() {
        let noteId = document.querySelector("#noteId");
        $.ajax({
            type: "POST",
            url: PRO_NAME + "/user/like/note",
            data: {"noteId": noteId.value},
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC){
                    if(data.data !== undefined){
                        let likeCount = document.querySelector("#like span");
                        like();
                        likeCount.innerHTML = data.data.likeCount;
                        findDislikeCount();
                        layer.msg("点赞成功", {
                            time: 2500
                        });
                    }
                }
            },
            async: true
        });
    }
    /**
     * 点踩
     */
    function dislikeNote() {
        let noteId = document.querySelector("#noteId");
        $.ajax({
            type: "POST",
            url: PRO_NAME + "/user/dislike/note",
            data: {"noteId": noteId.value},
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC){
                    if(data.data !== undefined){
                        let dislikeCount = document.querySelector("#dislike span");
                        dislike();
                        dislikeCount.innerHTML = data.data.dislikeCount;
                        findLikeCount();
                        layer.msg("点踩成功", {
                            time: 2500
                        });
                    }
                }
            },
            async: true
        });
    }
    /**
     * 点亮点赞图标
     */
    function like() {
        let like = document.querySelector("#like");
        let dislike = document.querySelector("#dislike");

        like.style.color = "rgba(66, 211, 132, .83)";
        like.title = "已点赞";
        dislike.style.color = "rgba(54, 58, 57, .7)";
        dislike.title = "踩";
    }
    /**
     * 点亮踩图标
     */
    function dislike() {
        let dislike = document.querySelector("#dislike");
        let like = document.querySelector("#like");

        dislike.style.color = "rgba(255, 63, 75, 1)";
        dislike.title = "已踩";
        like.style.color = "rgba(54, 58, 57, .7)";
        like.title = "赞";
    }

    /**
     * 是否已收藏
     */
    function getCollectStatus() {
        let noteId = document.querySelector("#noteId");
        $.ajax({
            type: "GET",
            url: PRO_NAME + "/note/collectstatus",
            data: {"noteId": noteId.value},
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC){
                    if(data.data !== undefined){
                        let status = data.data.collectStatus;
                        if (status === 1) {
                            collect();
                        } else if(status === -1){
                            uncollect();
                        }
                    }
                }
            },
            async: true
        });
    }
    /**
     * 收藏
     */
    function collectNote() {
        let noteId = document.querySelector("#noteId");
        $.ajax({
            type: "POST",
            url: PRO_NAME + "/user/collect/note",
            data: {"noteId": noteId.value},
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC){
                    if(data.data !== undefined){
                        let collectStatus = data.data.collectStatus;
                        if (collectStatus === 1) {
                            collect();
                            layer.msg("收藏成功", {
                                time: 2500
                            });
                        } else {
                            uncollect();
                            layer.msg("已取消收藏", {
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
     * 改变收藏图标为已收藏
     */
    function collect() {
        let favorite = document.querySelector("#favorite");
        let favoriteIcon = document.querySelector("#favorite-icon");

        favorite.title = "已收藏";
        favoriteIcon.style.color = "rgba(255, 210, 0, 1)";
    }
    /**
     * 改变收藏图标为未收藏
     */
    function uncollect() {
        let favorite = document.querySelector("#favorite");
        let favoriteIcon = document.querySelector("#favorite-icon");

        favorite.title = "收藏TA";
        favoriteIcon.style.color = "rgba(54, 58, 57, .7)";
    }

    /**
     * 发表评论
     */
    function addComment(kindEditor, noteId, content, parentId) {
        if (content == null || content.length <= 0) {
            layer.msg("评论内容不能为空", {
                time: 2000, //默认是3秒
            });
            return;
        }
        //向后台发送ajax请求发布评论
        $.ajax({
            type: "POST",
            url: PRO_NAME + "/user/note/comment",
            data: {
                "noteId": noteId,
                "content": content,
                "parentId": parentId
            },
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC){
                    kindEditor.html("");
                    layer.msg("评论成功", {
                        time: 2500
                    });
                    //更新评论区
                    findComments();
                }else{
                    layer.msg("评论失败", {
                        time: 2500
                    });
                }
            },
            async: true
        });
    }
    /**
     * 查找手记的评论
     */
    function findComments() {
        let noteId = document.querySelector("#noteId");
        $.ajax({
            type: "GET",
            url: PRO_NAME + "/note/comments",
            data: {
                "noteId": noteId.value
            },
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC){
                    if(data.data !== undefined && data.data.comments.length > 0){
                        showComments(data.data.comments);
                        showCommentFunc();
                    }else{
                        //todo 完善空评论区
                        let commentBox = document.querySelector("#comments");
                        commentBox.innerHTML = "<div id='no-comments'>" +
                            "<span id='no-comments-icon'>" +
                            "<svg class='icon' aria-hidden='true'>" +
                            "<use xlink:href='#icon-shafa1'></use>" +
                            "</svg>" +
                            "</span>"+
                            "<span id='no-comments-text'>第一个赶到的你，赶紧来<a href='#write-comment-box'>抢沙发</a>吧</span>"
                        "</div>";
                    }
                }
            },
            async: true
        });
    }

    /**
     * 展示评论区
     */
    function showComments(comments) {
        //获得评论区
        let commentBox = document.querySelector("#comments");
        commentBox.innerHTML = "";
        for (let i = 0; i < comments.length; i++) {
            if (comments[i] != null) {
                let createTime = util.timeAgo(comments[i].createTime, false);
                //创建一条评论
                let comment = document.createElement("div");
                addClass("comment", comment);
                //创建评论者头像
                let observerImg = document.createElement("img");
                addClass("comment-profile", observerImg);
                observerImg.src = comments[i].user.profilePath;
                //创建父评论
                let observer = document.createElement("div");
                addClass("comment-rightpart", observer);
                observer.innerHTML = "<time class='comment-time'>" + createTime + "</time>" +
                    "<a href='/user/"+ comments[i].user.nickname +"' class='comment-nickname'>" + comments[i].user.nickname + "</a>" +
                    "<span class='comment-content'>" + comments[i].content +
                    "<a class='comment-func' data-owner-id='"+ comments[i].user.userId +"' data-id='"+ comments[i].id +"' data-nickname='"+ comments[i].user.nickname +"' data-comment-content='"+ comments[i].content +"'>" +
                    "<svg class='icon' aria-hidden='true'>" +
                    "<use xlink:href='#icon-unie6a52'></use>" +
                    "</svg>" +
                    "</a>" +
                    "</span>";
                //将主评论插入到评论
                comment.appendChild(observerImg);
                comment.appendChild(observer);

                if (comments[i].childComments != null) {
                    showReplies(comment, comments[i], comments[i].childComments);
                }
                //将评论插入评论区
                commentBox.appendChild(comment);
            }
        }
    }

    //创建子评论（即回复）
    function showReplies(comment, parentComment, childComment) {
        for (let j = 0; j < childComment.length; j++) {
            let createTime = util.timeAgo(childComment[j].createTime, false);
            //创建回复
            let replyBox = document.createElement("div");
            addClass("replies", replyBox);
            replyBox.innerHTML = "<img src='" + childComment[j].user.profilePath + "' class='comment-profile'>" +
                "<time class='reply-time'>" + createTime + "</time>" +
                "<a href='/user/"+ childComment[j].user.userId +"' class='comment-nickname'>" + childComment[j].user.nickname + "</a><span class='reply-text'>回复</span><a href='#' class='comment-nickname'>" + parentComment.user.nickname + "</a>" +
                "<span class='comment-content'>" + childComment[j].content +
                "<a class='comment-func' data-owner-id='"+ childComment[j].user.userId +"' data-id='"+ childComment[j].id +"' data-nickname='"+ childComment[j].user.nickname +"' data-comment-content='"+ childComment[j].content +"'>" +
                "<svg class='icon' aria-hidden='true'>" +
                "<use xlink:href='#icon-unie6a52'></use>" +
                "</svg>" +
                "</a>" +
                "</span>";
            comment.appendChild(replyBox);
            //如果该评论还有子评论，就继续递归创建子评论
            if (childComment[j].childComments != null) {
                //传递父评论跟子评论过去，进行递归调用
                showReplies(comment, childComment[j], childComment[j].childComments);
            }
        }
    }

    /**
     * 渲染评论可用自定义功能
     */
    function showCommentFunc() {
        let currentUserId = document.querySelector("#current-user").value;
        let commentFuncBtn = document.querySelectorAll(".comment-func");
        for(let i=0; i<commentFuncBtn.length; i++){
            commentFuncBtn[i].addEventListener("click", function (e) {
                if (hasLogin()) {
                    let ownerId = commentFuncBtn[i].getAttribute("data-owner-id");
                    let bereplyId = commentFuncBtn[i].getAttribute("data-id");
                    let bereplyNickname = commentFuncBtn[i].getAttribute("data-nickname");
                    let commentContent = commentFuncBtn[i].getAttribute("data-comment-content");
                    //判断当前评论是否属于本用户
                    let content;
                    if(ownerId === currentUserId){
                        content = "<a class='comment-func-point reply' data-id='"+ bereplyId +"' data-nickname='"+ bereplyNickname +"' data-comment-content='"+ commentContent +"'>回复</a><br>" +
                            "<a class='comment-func-point report-comment'>举报</a><br>" +
                            "<a class='comment-func-point delete-comment' data-id='"+ bereplyId +"'>删除</a>";
                    }else{
                        content = "<a class='comment-func-point reply' data-id='"+ bereplyId +"' data-nickname='"+ bereplyNickname +"' data-comment-content='"+ commentContent +"'>回复</a><br>" +
                            "<a class='comment-func-point report-comment'>举报</a>";
                    }
                    layer.tips(content, this, {
                        time: 8000,
                        success: function (index, layero) {
                            //todo 当鼠标点击其它位置时，关闭tips
                        }
                    });
                    replyTo();
                    reportComment();
                    deleteComment();
                } else {
                    showNeedLogin(layer, "小主，要登录才能回复哦 (,,・ω・,,)", getUrlRelativePath());
                }
            })
        }
    }
    /**
     * 回复
     */
    function replyTo() {
        let noteId = document.querySelector("#noteId");
        let replyBtns = document.querySelectorAll(".reply");
        for(let i=0; i<replyBtns.length; i++){
            replyBtns[i].addEventListener("click", function (e) {
                if (hasLogin()) {
                    let bereplyId = replyBtns[i].getAttribute("data-id");
                    let bereplyNickname = replyBtns[i].getAttribute("data-nickname");
                    let commentContent = replyBtns[i].getAttribute("data-comment-content");
                    //todo 完善回复功能
                    layer.open({
                        type: 2,
                        title: '回复【'+bereplyNickname+'】的评论【'+ commentContent +'】',
                        shadeClose: true,
                        shade: 0.3,
                        area: ['420px', '260px'],
                        content: '/user/note/reply/'+ noteId.value +"/"+ bereplyId,
                        end: function () {
                            findComments();
                        }
                    });
                } else {
                    showNeedLogin(layer, "小主，要登录才能评论哦 (,,・ω・,,)", getUrlRelativePath());
                }
            });
        }
    }
    /**
     * 举报评论
     */
    function reportComment() {
        let replyBtns = document.querySelectorAll(".reply");
        let reportBtns = document.querySelectorAll(".report-comment");
        for(let i=0; i<reportBtns.length; i++){
            reportBtns[i].addEventListener("click", function (e) {
                if(hasLogin()){
                    let bereplyId = replyBtns[i].getAttribute("data-id");
                    let bereplyNickname = replyBtns[i].getAttribute("data-nickname");
                    let commentContent = replyBtns[i].getAttribute("data-comment-content");
                    layer.open({
                        type: 2,
                        title: '举报【'+bereplyNickname+'】的评论【'+commentContent+'】',
                        shadeClose: true,
                        shade: 0.3,
                        area: ['420px', '300px'],
                        content: '/user/report/'+ ENTITY_COMMENT + "/" + bereplyId,
                        end: function () {

                        }
                    });
                }else{
                    showNeedLogin(layer, "小主，要登录才能举报哦 (,,・ω・,,)", getUrlRelativePath());
                }
            })
        }
    }
    /**
     * 删除评论
     */
    function deleteComment() {
        let deleteBtns = document.querySelectorAll(".delete-comment");
        for(let i=0; i<deleteBtns.length; i++){
            deleteBtns[i].addEventListener("click", function (e) {
                //todo 询问是否删除
                if(hasLogin()){
                    let commentId = deleteBtns[i].getAttribute("data-id");
                    $.ajax({
                        type: "POST",
                        url: PRO_NAME + "/user/comment/delete",
                        data: {"commentId": commentId},
                        dataType: "json",
                        success: function (data) {
                            if(data.code === REQ_SUCC){
                                findComments();
                                layer.msg("评论已删除", {
                                    time: 2500
                                });
                            }else{
                                layer.msg("删除评论失败", {
                                    time: 2500
                                });
                            }
                        },
                        async: true
                    });
                }else{
                    showNeedLogin(layer, "小主，要登录才能删除哦 (,,・ω・,,)", getUrlRelativePath());
                }
            })
        }
    }
})