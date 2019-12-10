layui.use(['layer', 'laypage', 'carousel', 'util'], function () {
    let layer = layui.layer,
        carousel = layui.carousel,
        util = layui.util,
        laypage = layui.laypage;

    notePage();
    getHotNotes();
    showQRCode();
    showQRCode1();

    carousel.render({
        elem: '#ad',
        width: '100%',
        height: '140px',
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

    //反馈
    let feedback = document.querySelector("#feedback"), feedbackTips;
    feedback.addEventListener("click", function (e) {
        if(hasLogin()){
            layer.open({
                type: 1,
                title: '反馈 or 建议',
                shadeClose: false,
                shade: 0.6,
                area: ['300px', '300px'],
                content:
                    '<div style="width: 100%; padding: 10px 15px">' +
                    '<textarea id="feedback-content" placeholder="请输入内容" class="layui-textarea" style="height: 180px;"></textarea>' +
                    '</div>',
                btn: ['提交反馈'],
                yes: function(index, layero){
                    let content = document.querySelector("#feedback-content");
                    if(!isNullStr(content.value)){
                        $.ajax({
                            type: "POST",
                            data: {"content":content.value},
                            dataType: "json",
                            url: "/user/feedback" ,
                            success : function(data){
                                if(data.code === REQ_SUCC){
                                    layer.close(index);
                                    layer.msg("反馈成功", {
                                        time: 2500
                                    });
                                }else{
                                    layer.msg(data.code + ":" + data.msg, {
                                        time: 2500
                                    });
                                }
                            }
                        });
                    }else{
                        layer.msg("反馈内容不能为空", {
                            time: 2500
                        });
                    }
                }
            });
        }else{
            showNeedLogin(layer, "小主，要登录才能反馈哦 (,,・ω・,,)", getUrlRelativePath());
        }
    });

    //跳转到顶部
    let upToTop = document.querySelector("#uptoTop");
    upToTop.addEventListener("click", function (e) {
        $("body,html").animate({scrollTop: 0}, 300);
    });

    function notePage(){
        $.ajax({
            type: "GET",
            dataType: "json",
            url: "/notes/count" ,
            success : function(data){
                if(data.code === REQ_SUCC){
                    laypage.render({
                        elem: 'note-laypage',
                        count: data.data.count, //数据总数
                        limit: 8,
                        curr: 1,
                        groups: 3,
                        jump: function(obj, first){
                            findAllNotes((obj.curr-1)*obj.limit, obj.limit);
                        }
                    });
                }else{
                    layer.msg(data.code + ":" + data.msg, {
                        time: 2500
                    });
                }
            }
        });
    }

    /**
     * 查找“优质手记”
     */
    function findAllNotes(offset, limit) {
        let noteSection = document.querySelector("#noteSection");
        let loadEleId = partLoading(noteSection, createRandomId());
        $.ajax({
            type: "GET",
            url: PRO_NAME + "/notes",
            dataType: "json",
            data: {"offset":offset,"limit":limit},
            success: function (data) {
                if (data.code === REQ_SUCC) {
                    printRetMsg(data.code, data.msg);
                    let noteContentTemp = "";
                    let notes = data.data.notes;
                    for (let i = 0; i < notes.length; i++) {
                        let createTime = util.toDateString(notes[i].createTime, 'yyyy-MM-dd HH:mm');
                        noteContentTemp = noteContentTemp +
                            "<article class='article'>" +
                            "<h2><a href='/note/"+ notes[i].id +"' title='"+ notes[i].title +"'>"+ notes[i].title +"</a></h2>" +
                            "<p>点击进入详细阅读</p>" +
                            "<div class='articleInfo'>" +
                            "<div class='author-infobox'>" +
                            "<a href='/user/"+ notes[i].userId +"' class='author' data-author-id='"+ notes[i].userId +"'>"+ notes[i].authorNickname +"</a>" +
                            "</div>" +
                            "<a class='article-type'>"+ notes[i].type +"</a>" +
                            "<time>"+ createTime +"</time>" +
                            "<div class='readInfo'>" +
                            "<span><a>浏览量</a>"+ notes[i].readCnt +"</span>" +
                            "<span><a>评论</a>"+ notes[i].commentCnt +"</span>" +
                            "</div>" +
                            "</div>" +
                            "</article>";
                    }
                    noteSection.innerHTML = noteContentTemp;
                    //showAuthorInfo();
                }else{
                    layer.msg(data.code + ":" + data.msg, {
                        time: 2500
                    });
                }
            },
            complete: function(){
                loaded(noteSection, document.getElementById(loadEleId));
            },
            async: true
        });
    }

    function getHotNotes() {
        $.ajax({
            type: "GET",
            url: PRO_NAME + "/notes/hot",
            dataType: "json",
            data: {
                "offset":0,
                "limit":10
            },
            success: function (data) {
                if (data.code === REQ_SUCC && data.data != null && data.data.notes != null) {
                    console.log(data.data);
                    let notes = data.data.notes;
                    let hotNotesBlock = document.querySelector("#hot-notes");
                    let hotNotesHtml = "";
                    let rank;
                    for(let i=0, len=notes.length; i<len; i++){
                        rank = i + 1;
                        hotNotesHtml = hotNotesHtml +
                            "<div class='hot-note-item'>" +
                            "<span>"+ rank +"</span>" +
                            "<a href='/note/"+ notes[i].id +"' title='标题：" + notes[i].title + TAKEIN + "作者："+ notes[i].nickname +"'>"+ notes[i].title +"</a>" +
                            "</div>";
                    }
                    hotNotesBlock.innerHTML = hotNotesHtml;
                }
            },
            complete: function(){

            },
            async: true
        });
    }

    /**
     * 显示微信账号二维码信息
     */
    function showQRCode() {
        let wechat = document.querySelector("#wechat");
        let wechatQRC = wechat.querySelector("img");
        let arrow = wechat.querySelector("span");
        wechat.onmouseover = function () {
            wechatQRC.style.display = "block";
            arrow.style.display = "block";
        }
        wechat.onmouseout = function () {
            wechatQRC.style.display = "none";
            arrow.style.display = "none";
        }
    }

    /**
     * 显示QQ账号二维码信息
     */
    function showQRCode1() {
        let qq = document.querySelector("#qq");
        let qqQRC = qq.querySelector("img");
        let arrow = qq.querySelector("span");
        qq.onmouseover = function () {
            qqQRC.style.display = "block";
            arrow.style.display = "block";
            arrow.style.borderTopColor = "rgba(246,246,246,1)";
        }
        qq.onmouseout = function () {
            qqQRC.style.display = "none";
            arrow.style.display = "none";
        }
    }
});

/*==========显示用户信息=========*/
function showAuthorInfo() {
    let authorBtns = document.querySelectorAll(".author");
    let lastTarget;
    for(let i=0; i<authorBtns.length; i++){
        authorBtns[i].addEventListener("mouseover", function (e) {
            if(lastTarget === authorBtns[i]) return;
            lastTarget = authorBtns[i];
            //ajax获取用户信息
            $.ajax({
                type: "GET",
                url: PRO_NAME + "/popupuser/"+authorBtns[i].getAttribute("data-author-id"),
                dataType: "json",
                success: function (data) {
                    if((data.code === REQ_SUCC) && (data.data !== undefined)){
                        let user = data.data.user;
                        let content = "<div id='userinfo-box' class='user-info'>" +
                            "<div class='user-basic-info'>" +
                            "<div class='user-basic-infobg'>" +
                            "<img src='"+ user.profilePath +"' class='user-profile'>" +
                            "<span class='user-nickname'>"+ user.nickname +"</span>" +
                            "</div>" +
                            "</div>" +
                            "<div class='user-devote'>" +
                            "<span>手记<em>"+ user.noteCnt +"</em>篇</span>" +
                            "<span>粉丝<em>"+ user.followerCnt +"</em>位</span>" +
                            "<a id='attentionBtn' class='pay-attention-to' data-followee-id='"+ user.userId +"'>关注TA</a>" +
                            "</div>" +
                            "</div>";
                        layer.tips(content, authorBtns[i], {
                            tips:1,
                            area:['300px', '200px'],
                            skin: 'layer-ext-blogskin1',
                            time: 0,
                            success: function (layero, index) {
                                document.addEventListener("click", function (e) {
                                    let event = e || window.event;
                                    let from = event.target;
                                    let parent = document.querySelector("#userinfo-box");
                                    if (parent && parent.contains(from)) {
                                        return;
                                    }
                                    layer.close(index);
                                });
                            }
                        });
                    }
                },
                async: true
            });
        });

        authorBtns[i].addEventListener("mouseout", function(e){
            console.log("离开");
            //layer.close(index);
        });
    }
}

/*===================*/

/**
 * 显示作者详细信息
 * <div class='author-info'>
 *     <div class='author-basic-info'>
 *         <div class='author-basic-infobg'>
 *             <img src='../image/csdn.png' class='author-profile'>
 *             <span class='author-nickname'>CSDN</span>
 *             <span class='author-job'>专业IT学习网站</span>
 *         </div>
 *     </div>
 *     <div class='author-devote'>
 *         <span>手记<em>9</em>篇</span><span>解答<em>9</em>次</span>
 *         <button>关注ta</button>
 *     </div>
 * </div>
 */