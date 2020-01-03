layui.use(['layer', 'layedit', 'flow', 'carousel', 'util', 'laytpl'], function () {
    let layer = layui.layer,
        layedit = layui.layedit,
        carousel = layui.carousel,
        flow = layui.flow,
        util = layui.util,
        laytpl = layui.laytpl;

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

    //相关推荐轮播图
    carousel.render({
        elem: '#recommend-carousel',
        width: '100%',
        height: '150px',
        arrow: 'hover',
        indicator: 'inside'
    });
    //设置layui编辑器图片上传路径
    layedit.set({
        uploadImage: {
            url: '/user/file/feedpic',
            type: 'post'
        }
    });
    let original = layedit.build('editor-input', {
        height: 100,
        tool: [
            'face', //表情
            'feedImage' //动态图片
        ],
        btn: '<span class="layui-layedit-submit-btn" id="editor-submit">发布</span>'
    });

    //为“发布动态”按钮绑定事件
    let isClickSendBtn = true;
    let sendBtn = document.querySelector("#editor-submit");
    sendBtn.addEventListener("click", function (e) {
        if(isClickSendBtn){
            isClickSendBtn = false;
            setTimeout(function () {
                isClickSendBtn = true;
            }, 1500);
            //同步编辑器内容
            layedit.sync(original);
            let formData = new FormData(),
                content = document.querySelector("#editor-input").value,
                attachment = '',
                srcArrInput = document.querySelector("#upload-file-array");
            //若是图片动态，则获取图片信息
            if(srcArrInput && !isNullStr(srcArrInput.value)){
                attachment = srcArrInput.value;
            }
            if(content == null && content.length <= 0 && attachment === '' && attachment.length <= 0){
                layer.msg("动态内容不能为空", {
                    time: 2500
                });
                return;
            }
            formData.append("content", content);
            formData.append("attachment", attachment);
            let index = layer.load(1, {
                shade: [0.4,'#000'] //0.1透明度的白色背景
            });
            $.ajax({
                type: "post",
                url: "/user/feed/original",
                data: formData,
                processData: false,   //必须
                contentType: false,   //必须
                success: function (data) {
                    layer.close(index);
                    if(data.code === REQ_SUCC){
                        layer.msg("动态发送成功", {
                            time: 2500
                        });
                        layedit.setContent(original, "", false);
                        window.location.reload();
                    }else{
                        layer.msg(data.code + ":" + data.msg, {
                            time: 2500
                        });
                    }
                }
            });
        }
    });

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

    /**
     * 举报动态
     */
    $(document).on("click", ".report-feed", function () {
        let feedId = $(this).attr("data-id");
        layer.open({
            type: 2,
            title: false,
            shadeClose: true,
            shade: 0.4,
            area: ['420px', '300px'],
            content: '/user/report/'+ ENTITY_FEED + "/" + feedId,
            end: function () {
            }
        });
    });

    flow.load({
        elem: '#feeds-block',
        scrollElem: document,
        isAuto: true,
        mb: 150,
        end: "已经到底了 ㄟ(≧◇≦)ㄏ",
        done: function(page, next){
            $.ajax({
                type: "GET",
                url: PRO_NAME + "/user/feed/detail",
                data: {
                    "page": page,
                    "limit": 6
                },
                dataType: "json",
                success: function (data) {
                    if(data.code === REQ_SUCC && data.data != null && data.data.feeds != null){
                        let lis = [], flag, attachment, //flag判断被转发动态是否为空（已被删除），attachment为附带内容
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
                    }else{
                        let notesBox = document.querySelector("#feeds-block");
                        notesBox.innerHTML = "<div id='no-content-box'><span id='no-content-icon'><svg class='icon' aria-hidden='true'><use xlink:href='#icon-kong'></use></svg></span><span id='no-content'>空空如也，用户暂无动态</span></div>";
                    }
                },
                async: true
            });
        }
    });
});