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
                srcArrInput = document.querySelector("#upload-file-array");
            //若是图片动态，则获取图片信息
            if(srcArrInput && !isNullStr(srcArrInput.value)){
                console.log(srcArrInput.value);
                let srcArr = srcArrInput.value.split(","),
                    feedImgContent = "<span class='feed-item-content-img-box'>";
                for(let i=0; i<srcArr.length; i++){
                    feedImgContent = feedImgContent + "<img src='"+ srcArr[i] +"' class='feed-item-content-img' title='查看图片'>";
                }
                feedImgContent = feedImgContent + "</span>";
                content = content + feedImgContent;
            }
            if(content == null || content.length <= 0){
                layer.msg("动态内容不能为空", {
                    time: 2500
                });
                return;
            }
            formData.append("content", content);
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
                        let lis = [], currentUser = document.querySelector("#current-user").value,
                            self, flag, //self是否为用户本人动态，flag判断被转发动态是否为空（已被删除）
                            forwordFeed, forwordFeedCreateDate, forwordFeedContent; //被转发动态信息
                        layui.each(data.data.feeds, function(index, item){
                            let createTime = util.timeAgo(item.createdDate, false);
                            let map = JSON.parse(item.data);
                            self = parseInt(currentUser) === parseInt(item.user.userId);
                            let renderData,
                                template;
                            if(item.type === 1){
                                renderData = { //数据
                                    self: self,
                                    type: item.type,
                                    feed: item,
                                    createTime: createTime,
                                    map: map,
                                    forwordCntId: "forword-cnt-"+item.id,
                                    commentCntId: "comment-cnt-"+item.id,
                                    likeCntId: "like-cnt-"+item.id,
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
                                    forwordFeedContent = JSON.parse(map.feed.data).content;
                                }else{
                                    flag = false;
                                    forwordFeed = "";
                                    forwordFeedCreateDate = "";
                                    forwordFeedContent = "该条动态已被删除 (ﾒﾟДﾟ)ﾒ";
                                }
                                renderData = { //数据
                                    self: self,
                                    type: item.type,
                                    feed: item,
                                    createTime: createTime,
                                    content: map.content,

                                    flag: flag,
                                    forword: forwordFeed,
                                    forwordCreateTime: forwordFeedCreateDate,
                                    forwordContent: forwordFeedContent,

                                    forwordCntId: "forword-cnt-"+item.id,
                                    commentCntId: "comment-cnt-"+item.id,
                                    likeCntId: "like-cnt-"+item.id,
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
                },
                async: true
            });
        }
    });
});

/*
    let imgBar = document.querySelector("#img-bar"),
        uploadFeedImg = document.querySelector("#feed-img-input");
    uploadFeedImg.addEventListener("change", function (e) {
        setImagePreview(this, imgBar);
    });

    let imgNum = 0, curFiles = [];      //存放上传图片列表
    function setImagePreview(uploadFeedImgEle, imgBar) {
        for(let i=0; i<9; i++){
            if(curFiles.length >= 9){
                layer.msg("最多只能上传9张图片哦", {
                    time: 2500
                });
                return;
            }
            if (uploadFeedImgEle.files && uploadFeedImgEle.files[i]) {
                let imgObjPreview = new Image();
                imgObjPreview.src = getImgUrl(uploadFeedImgEle.files[i]);
                let newImg = document.createElement("span");    //图片栏添加图片预览
                newImg.classList.add("delete-img-btn");
                newImg.title = "点击删除该图片";
                newImg.appendChild(imgObjPreview);
                imgBar.insertBefore(newImg, imgBar.firstElementChild);
                newImg.onclick = function (e) {
                    removeImg(this, uploadFeedImgEle.files[i].name);
                };
                curFiles.push(uploadFeedImgEle.files[i]);
                imgNum++;
            }
        }
    }
    //移除图片
    function removeImg(ele, filename) {
        let imgBar = document.querySelector("#img-bar");
        imgBar.removeChild(ele);
        curFiles = curFiles.filter(function(file) {
            return file.name !== filename;
        });
        console.log(curFiles);
    }
    //获取input[file]图片的url
    function getImgUrl(file) {
        let url;
        let agent = navigator.userAgent;
        if (agent.indexOf("MSIE")>=1) {
            url = file.value;
        } else if(agent.indexOf("Firefox")>0) {
            url = window.URL.createObjectURL(file);
        } else if(agent.indexOf("Chrome")>0) {
            url = window.URL.createObjectURL(file);
        }
        return url;
    }
    */