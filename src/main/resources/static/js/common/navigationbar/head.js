layui.use(['layer', 'flow', 'util', 'laytpl', 'element'], function () {
    let layer = layui.layer,
        flow = layui.flow,
        util = layui.util,
        laytpl = layui.laytpl,
        element = layui.element;

    //初始化
    checkLogin();

    //下滚导航栏吸顶
    let t= 42, p = 0,
        header = document.querySelector("#header");   // 目前监听的是整个body的滚动条距离
    window.addEventListener("scroll", function (e) {
        p = $('body, html').scrollTop();
        if(t<p){   //下滚
            console.log("下滚");
            header.classList.add("menu-active");
        }else{  //上滚
            console.log("上滚");
            header.classList.remove("menu-active");
        }
        setTimeout(function(){t = p;},0);
    });

    /**
     * 原创方式动画
     */
    function creationAnim(){
        let creation = document.querySelector("#creation"),
            creationWays = document.querySelector("#creation-ways-box");
        //鼠标悬浮
        creation.addEventListener('mouseover', function (e) {
            e = window.event || e;
            let s = e.fromElement || e.relatedTarget;
            if (!this.contains(s)) {
                creationWays.classList.remove("hideCreaWays");
                creationWays.classList.add("showCreaWays");
            }
        }, false);
        //鼠标移开
        creation.addEventListener('mouseout', function (e) {
            e = window.event || e;
            let s = e.toElement || e.relatedTarget;
            if (!this.contains(s)) {
                creationWays.classList.remove("showCreaWays");
                creationWays.classList.add("hideCreaWays");
            }
        }, false);
    }

    /**
     * 定期执行函数
     */
    function regularInterval() {
        getMsgUnreadCntAll();

        setInterval(function () {
            getMsgUnreadCntAll();
        }, 8000);
    }

    /**
     * 检测是否已登录
     */
    function checkLogin() {
        $.ajax({
            type: "GET",
            dataType: "json",
            url: "/haslogin" ,
            success : function(data){
                let menu = document.querySelector("#menu"),
                    renderData, template;
                if(data.data.status === 1){
                    //已登录
                    renderData = {
                        userId: data.data.user.userId,
                        profile: data.data.user.profilePath,
                        nickname: data.data.user.nickname
                    };
                    template = document.querySelector("#logined-template").innerHTML;
                    laytpl(template).render(renderData, function(html){
                        menu.innerHTML = menu.innerHTML + html;
                    });
                    //已登录执行操作
                    creationAnim();
                    regularInterval();
                    getMsgUnreadCntEach();
                    showUserDetail();
                    showFavoriteContentBrief();
                    showTimelineBrief();
                }else{
                    //未登录
                    renderData = {};
                    template = document.querySelector("#unlogin-template").innerHTML;
                    laytpl(template).render(renderData, function(html){
                        menu.innerHTML = menu.innerHTML + html;
                    });
                }
                //element.render();
            },
            async: true
        });
    }

    /**
     * 鼠标悬浮通过ajax查询用户详细信息并显示
     */
    function showUserDetail() {
        let userinfo = document.querySelector("#user");
        if (userinfo != null) {
            let userDetail = document.querySelector('#user_detail');
            //鼠标悬浮
            userinfo.addEventListener('mouseover', function (e) {
                userDetail.style.display = 'block';
            }, false);
            //鼠标移开
            userinfo.addEventListener('mouseout', function (e) {
                userDetail.style.display = 'none';
            }, false);
        }
    }

    function getMsgUnreadCntAll() {
        $.ajax({
            type: "GET",
            url: PRO_NAME + "/user/conversations/allunread",
            dataType: "json",
            success: function (data) {
                //获得未读消息数
                if(data.code === REQ_SUCC){
                    let allunread = document.querySelector("#msg-allunread");
                    allunread.innerHTML = (data.data.allunread === 0) ? "" : data.data.allunread;
                }
            },
            async: true
        });
    }
    /**
     * 鼠标悬浮通过ajax查询未读消息
     */
    function getMsgUnreadCntEach() {
        let message = document.querySelector("#message");
        if (message != null) {
            let mymessage = document.querySelector('#mymessage');
            //鼠标悬浮
            message.addEventListener('mouseover', function (e) {
                e = window.event || e;
                let s = e.fromElement || e.relatedTarget;
                if (!this.contains(s)) {
                    mouseoverFun();
                }
            }, false);
            //onmouseover事件处理函数
            function mouseoverFun(){
                mymessage.style.display = 'block';
                queryUnreadMessageCnt();
            }

            //鼠标移开
            message.addEventListener('mouseout', function (e) {
                e = window.event || e;
                let s = e.toElement || e.relatedTarget;
                if (!this.contains(s)) {
                    mouseoutFun();
                }
            }, false);
            //onmouseout事件处理函数
            function mouseoutFun() {
                mymessage.style.display = 'none';
            }
        }

        //获取未读消息数
        function queryUnreadMessageCnt() {
            //私信我的
            ajaxUnreadCnt(1, "#msg-pm-unread");
            //关注我的
            ajaxUnreadCnt(2, "#msg-follow-unread");
            //点赞我的
            ajaxUnreadCnt(3, "#msg-like-unread");
            //回复我的
            ajaxUnreadCnt(4, "#msg-comment-unread");
            //系统通知
            ajaxUnreadCnt(0, "#msg-sys-unread");
        }
        //Ajax请求获取未读消息数
        function ajaxUnreadCnt(type, id) {
            $.ajax({
                type: "GET",
                url: PRO_NAME + "/user/conversations/unread",
                dataType: "json",
                data: {"type":type},
                success: function (data) {
                    if(data.data != null){
                        fillData(id, data.data.unread);
                    }
                },
                async: true
            });
        }
        //填充数据
        function fillData(id, unread) {
            let unreadEle = document.querySelector(id);
            unreadEle.innerHTML = unread === 0 ? "" : unread;
        }
    }

    /**
     * 收藏夹
     */
    function showFavoriteContentBrief() {
        let favorites = document.querySelector("#favorites");
        if (favorites != null) {
            let favoriteItems = document.querySelector("#favorite-items");
            //鼠标悬浮
            favorites.addEventListener("mouseover", function (e) {
                let event = e || window.event;
                let from = event.fromElement || event.relatedTarget;
                if (!this.contains(from)) {
                    favoriteItems.style.display = 'block';
                    $.ajax({
                        type: "GET",
                        url: PRO_NAME + "/user/favorite/brief",
                        data: {"offset":0,"limit":6},
                        dataType: "json",
                        success: function (data) {
                            if (data.code === REQ_SUCC && data.data !== undefined){
                                let notes = data.data.notes;
                                if(notes.length > 0){
                                    let favoritesContentTemp = "";
                                    for (let i = 0; i < notes.length; i++) {
                                        favoritesContentTemp = favoritesContentTemp +
                                            "<li><a href='/note/" + notes[i].id + "' title='标题：" + notes[i].title + TAKEIN + "作者："+ notes[i].nickname +"'>" + notes[i].title + "</a></li>";
                                    }
                                    favoriteItems.innerHTML = favoritesContentTemp + "<a href='/user/favorite' id='more-favorite'>查看更多</a>";
                                }else{
                                    favoriteItems.innerHTML = "<span id='no-favorite-icon'><svg class='icon' aria-hidden='true'><use xlink:href='#icon-zhaobudaojieguo'></use></svg></span><span id='no-favorite'>收藏夹里没有东西哦</span>";
                                }
                            }else {
                                layer.msg(data.code + ":" + data.msg, {
                                    time: 2500
                                });
                            }
                        },
                        async: true
                    });
                }
            }, false);
            //鼠标移开
            favorites.addEventListener('mouseout', function (e) {
                let event = window.event || e;
                let s = event.toElement || event.relatedTarget;
                if (!this.contains(s)) {
                    favoriteItems.style.display = 'none';
                    favoriteItems.innerHTML = "<span>loading...</span>";
                }
            }, false);
        }
    }

    /**
     * 动态
     */
    function showTimelineBrief() {
        let timeline = document.querySelector("#timeline");
        if (timeline != null) {
            let timelineItems = document.querySelector("#timeline-items");
            //鼠标悬浮
            timeline.addEventListener("mouseover", function (e) {
                let event = e || window.event;
                let from = event.fromElement || event.relatedTarget;
                if (!this.contains(from)) {
                    timelineItems.style.display = 'block';
                    flow.load({
                        elem: '#timeline-items',
                        scrollElem: '#timeline-items',
                        isAuto: true,
                        mb: 100,
                        end: "已经到底了 ㄟ(≧◇≦)ㄏ",
                        done: function(page, next){
                            $.ajax({
                                type: "GET",
                                url: PRO_NAME + "/user/feed/brief",
                                data: {
                                    "page": page,
                                    "limit": 6
                                },
                                dataType: "json",
                                success: function (data) {
                                    if(data.code === REQ_SUCC && data.data !== undefined && data.data.feeds !== undefined){
                                        let lis = [];
                                        layui.each(data.data.feeds, function(index, item){
                                            let createTime = util.timeAgo(item.createdDate, false);
                                            let map = JSON.parse(item.data);
                                            let title = "时间："+ createTime;
                                            if(item.type === 1){
                                                lis.push("<li data-id='"+ item.id +"' title='"+ title +"'><a href='/user/"+ item.userId +"'>"+ map.nickname +"</a>发布了新动态</li>");
                                            }else if(item.type === 2){
                                                lis.push("<li data-id='"+ item.id +"' title='"+ title +"'><a href='/user/"+ item.userId +"'>"+ map.nickname +"</a>转发了动态</a></li>");
                                            }
                                        });

                                        next(lis.join(''), page < data.data.pages);
                                    }
                                },
                                async: true
                            });
                        }
                    });
                }
            }, false);
            //鼠标移开
            timeline.addEventListener('mouseout', function (e) {
                let event = window.event || e;
                let s = event.toElement || event.relatedTarget;
                if (!this.contains(s)) {
                    timelineItems.style.display = 'none';
                }
            }, false);
        }
    }
})