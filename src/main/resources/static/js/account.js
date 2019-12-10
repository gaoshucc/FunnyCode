layui.use(['layer', 'element', 'carousel', 'form'], function () {
    let layer = layui.layer,
        element = layui.element,
        carousel = layui.carousel,
        form = layui.form;

    regularInterval();

    //相关推荐轮播图
    carousel.render({
        elem: '#recommend-carousel',
        width: '100%',
        height: '150px',
        arrow: 'hover',
        indicator: 'none'
    });

    let editBtn = document.querySelector("#edit-account-info");
    editBtn.addEventListener("click", function (e) {
        layer.open({
            type: 2,
            title: false,
            shadeClose: false,
            shade: 0.4,
            area: ['480px', '420px'],
            content: "/user/account/edit",
            end: function () {}
        });
    });

    let myProfile = document.querySelector("#my-profile");
    myProfile.addEventListener("click", function (e) {
        layer.photos({
            photos: '#profileItem',
            anim: 5 //0-6的选择，指定弹出图片动画类型，默认随机（请注意，3.0之前的版本用shift参数）
        });
    });

    showFollowees(document.querySelector("#followees"));
    element.on('tab(follow-list)', function (data) {
        let tab = document.querySelectorAll(".layui-this")[0];
        let tabId = tab.getAttribute("lay-id");
        if(tabId === "1"){
            showFollowees(document.querySelector("#followees"));
        }else if(tabId === "2"){
            showFollowers(document.querySelector("#followers"));
        }else{
            layer.msg("数据获取异常", {
                time: 2500
            });
        }
    });

    /**
     * 定期执行函数
     */
    function regularInterval() {
        showAccountBasicInfo();

        setInterval(function () {
            showAccountBasicInfo();
        }, 2000);
    }

    /**
     * 查询并展示账户信息
     */
    function showAccountBasicInfo() {
        $.ajax({
            type: "GET",
            url: PRO_NAME + "/user/account/basic",
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC){
                    if(data.data !== undefined){
                        let user = data.data.user;
                        //左边
                        let myProfile = document.querySelector("#my-profile");
                        let username = document.querySelector("#username");
                        let nickname = document.querySelector("#nickname");
                        let gender = document.querySelector("#gender");
                        let email = document.querySelector("#email");
                        let signature = document.querySelector("#signature");
                        //右边
                        let active = document.querySelector("#active");
                        let noteCnt = document.querySelector("#note-cnt");
                        let followeeCnt = document.querySelector("#followee-cnt");
                        let followerCnt = document.querySelector("#follower-cnt");
                        let experience = document.querySelector("#experience");
                        let level = document.querySelectorAll(".level");
                        //private Long userId;
                        myProfile.src = user.profilePath;
                        username.innerHTML = user.username;
                        nickname.innerHTML = user.nickname;
                        gender.innerHTML = user.gender === 1 ?
                            "<svg class='icon' aria-hidden='true' style='font-size: 16px'>" +
                            "<use xlink:href='#icon-nanxing'></use>" +
                            "</svg>" : user.gender === 2 ?
                                "<svg class='icon' aria-hidden='true' style='font-size: 16px'>" +
                                "<use xlink:href='#icon-nvxing'></use>" +
                                "</svg>" : "无";

                        email.innerHTML = user.email != null ? user.email : "无";
                        signature.innerHTML = user.motto != null ? user.motto : "无";
                        active.innerHTML = user.active;
                        noteCnt.innerHTML = user.noteCnt;
                        followeeCnt.innerHTML = user.followeeCnt;
                        followerCnt.innerHTML = user.followerCnt;
                        experience.innerHTML = user.experience;
                        for (let i=0; i<level.length; i++){
                            level[i].innerHTML = user.level;
                        }
                        form.val("basic-form", {
                            "qq-bind": user.qqBind,
                            "wechat-bind": user.wechatBind
                        });
                        bindSwitchListener();
                    }
                }
            },
            async: true
        });
    }

    /**
     * 监听绑定开关事件
     */
    function bindSwitchListener() {
        //监听QQ登录绑定开关
        form.on('switch(qq-bind)', function(data){
            if(data.elem.checked){
                //绑定QQ
                $.ajax({
                    type: "GET",
                    dataType: "json",
                    url: "/qq/url" ,
                    success : function(data){
                        if(data.code === REQ_SUCC){
                            let url = data.data.url;
                            openwindow(
                                url,
                                "_self",
                                550,
                                350
                            );
                        }else{
                            layer.msg(data.code + ":" + data.msg, {
                                time: 2500
                            });
                        }
                    }
                });
            }else{
                //解除绑定QQ
                $.ajax({
                    type: "POST",
                    dataType: "json",
                    url: "/user/qq/unbind" ,
                    success : function(data){
                        if(data.code === REQ_SUCC){
                            layer.msg("QQ解绑成功", {
                                time: 2500
                            });
                        }else{
                            layer.msg(data.code + ":" + data.msg, {
                                time: 2500
                            });
                        }
                    }
                });
            }
        });
        //todo 监听微信登录绑定开关
        form.on('switch(wechat-bind)', function(data){
            console.log(data.elem); //得到checkbox原始DOM对象
            console.log(data.elem.checked); //开关是否开启，true或者false
        });
    }

    /**
     * 查询并显示我关注的
     */
    function showFollowees(content) {
        $.ajax({
            type: "GET",
            url: PRO_NAME + "/user/followees",
            data: {"offset":0,"limit":10},
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC){
                    if(data.data !== undefined && data.data.followees.length > 0){
                        let followees = data.data.followees;
                        let contentTemp = "";
                        for(let i=0; i<followees.length; i++){
                            contentTemp = contentTemp +
                                "<span class='followee'><img src='"+ followees[i].profilePath +"'/><a href='/user/"+ followees[i].userId +"' data-followee-id='"+ followees[i].userId +"'>"+ followees[i].nickname +"</a><span class='cancel-follow' data-id='"+ followees[i].userId +"'>取消关注</span></span>";
                        }
                        content.innerHTML = contentTemp;
                        //为“取关”按钮添加事件
                        let cancelFollowBtns = document.querySelectorAll(".cancel-follow");
                        for(let i=0; i<cancelFollowBtns.length; i++){
                            cancelFollowBtns[i].addEventListener("click", function () {
                                unfollowUser(this.getAttribute("data-id"));
                            });
                        }
                    }else {
                        content.innerHTML = "<div class='no-content-box'><span class='no-content-icon'><svg class='icon' aria-hidden='true'><use xlink:href='#icon-kong'></use></svg></span><span class='no-content'>还没有关注任何人哦</span></div>";
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

    /**
     * 取关用户
     * @param userId 要取关用户的ID
     */
    function unfollowUser(userId) {
        $.ajax({
            type: "POST",
            url: PRO_NAME + "/user/unfollow/user",
            data: {"userId": userId},
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC && data.data !== undefined && data.data.status === -1){
                    showFollowees(document.querySelector("#followees"));
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
     * 查询并显示关注我的
     */
    function showFollowers(content) {
        $.ajax({
            type: "GET",
            url: PRO_NAME + "/user/followers",
            data: {"offset":0,"limit":10},
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC){
                    if(data.data !== undefined && data.data.followers.length > 0){
                        let followers = data.data.followers;
                        let contentTemp = "";
                        for(let i=0; i<followers.length; i++){
                            contentTemp = contentTemp +
                                "<span class='follower'><img src='"+ followers[i].profilePath +"'/><a href='/user/"+ followers[i].userId +"' class='follow-item' data-followee-id='"+ followers[i].userId +"'>"+ followers[i].nickname +"</a></span>";
                        }
                        content.innerHTML = contentTemp;
                    }else {
                        content.innerHTML = "<div class='no-content-box'><span class='no-content-icon'><svg class='icon' aria-hidden='true'><use xlink:href='#icon-kong'></use></svg></span><span class='no-content'>还没有人关注我哦</span></div>";
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