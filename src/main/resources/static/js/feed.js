layui.use(['layer', 'layedit', 'flow', 'carousel', 'util', 'laytpl'], function () {
    let layer = layui.layer,
        layedit = layui.layedit,
        carousel = layui.carousel,
        flow = layui.flow,
        util = layui.util,
        laytpl = layui.laytpl;

    //相关推荐轮播图
    carousel.render({
        elem: '#recommend-carousel',
        width: '100%',
        height: '150px',
        arrow: 'hover',
        indicator: 'inside'
    });

    /*layedit.set({
        uploadImage: {
            url: '/user/file/feedpic',
            type: 'post'
        }
    });*/
    let original = layedit.build('editor-input', {
        height: 100,
        tool: [
            'face' //表情
        ]
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
            layedit.sync(original);
            let content = document.querySelector("#editor-input").value;
            if(content === null || content.length <= 0){
                layer.msg("动态内容不能为空", {
                    time: 2500
                });
                return;
            }
            let index = layer.load(1, {
                shade: [0.4,'#000'] //0.1透明度的白色背景
            });
            let formData = new FormData();
            formData.append("content", content);
            for (let i = 0; i < curFiles.length; i++) {
                formData.append("fileArray", curFiles[i]);
            }
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
                        let lis = [], currentUser = document.querySelector("#current-user").value;
                        let self, flag; //flag判断被转发动态是否为空（已被删除）
                        let forwordFeed, forwordFeedCreateDate, forwordFeedContent; //被转发动态信息
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