layui.use(['layer', 'element', 'util', 'layedit', 'flow'], function () {
    let element = layui.element,
        layer = layui.layer,
        util = layui.util,
        layedit = layui.layedit,
        flow = layui.flow;

    //监听tab切换事件
    element.on('tab(sys-msg)', function (data) {
        showMessageByType();
    });

    toTargetTab();
    regularInterval();
    getConversations();

    //点击显示系统消息
    let systemMessage = document.querySelector("#system-message");
    systemMessage.addEventListener("click", function (e) {
        let rightside = document.querySelector("#rightside");
        let sysMsg = document.querySelector("#sys-msg");
        let p = rightside.children;
        for (let i =0, len=p.length; i<len; i++) {
            if(p[i] !== sysMsg){
                p[i].classList.remove("show");
            }else{
                p[i].classList.add("show");
            }
        }
        let plusers = document.querySelectorAll(".personal-letter-item");
        for(let i=0, len=plusers.length; i<len; i++){
            plusers[i].classList.remove("selected");
        }
    });

    //查找用户
    let plUser = document.querySelector("#pl-user");
    plUser.addEventListener("blur", function (e) {
        setTimeout(hideUsersDiv,200);
    });
    plUser.addEventListener("keydοwn", function (e) {
        clearInput();
    });
    plUser.addEventListener("keyup", function (e) {
        innerKeydown();
    });

    let plUserOptional = document.querySelector("#pl-user-optional");
    plUserOptional.addEventListener("click", function (e) {
        hideUsersDiv();
    });

    //根据消息类型切换消息选项卡
    function toTargetTab() {
        let msgType = document.querySelector("#msg-type").value;
        element.tabChange("sys-msg", msgType);
    }

    //定期执行函数
    function regularInterval() {
        //初始化
        queryUnreadMessageCnt();

        setInterval(function () {
            queryUnreadMessageCnt();
        }, 2000);
    }

    //获取未读消息数
    function queryUnreadMessageCnt() {
        //关注我的
        ajaxUnreadCnt(2, ".msg-follow-unread");
        //点赞我的
        ajaxUnreadCnt(3, ".msg-like-unread");
        //回复我的
        ajaxUnreadCnt(4, ".msg-comment-unread");
        //系统通知
        ajaxUnreadCnt(0, ".msg-sys-unread");
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
    function fillData(cla, unread) {
        let unreadEles = document.querySelectorAll(cla);
        for(let i=0; i<unreadEles.length; i++){
            unread === 0 ? hiddenBadge(unreadEles[i]) : showBadge(unreadEles[i], unread);
        }
    }
    //隐藏未读消息小红点
    function hiddenBadge(ele) {
        ele.classList.add("hide");
    }
    //显示未读消息小红点
    function showBadge(ele, unread) {
        ele.classList.remove("hide");
        ele.innerHTML = unread;
    }

    /**
     * 根据类型显示消息
     */
    function showMessageByType() {
        let tab = document.querySelectorAll(".layui-this")[0];
        let tabId = tab.getAttribute("data-type");
        let msgType;
        if(tabId === "2"){
            msgType = 2;
        }else if(tabId === "3"){
            msgType = 3;
        }else if(tabId === "4"){
            msgType = 4;
        }else{
            msgType = 0;
        }
        $.ajax({
            type: "GET",
            url: PRO_NAME + "/user/message",
            data: {
                "type": msgType,
                "offset": 0,
                "limit": 20
            },
            dataType: "json",
            success: function (data) {
                let tabContent = document.querySelectorAll(".layui-show")[0];
                if(data.code === REQ_SUCC && data.data != null && data.data.msgs != null && data.data.msgs.length > 0){
                    let msgs = data.data.msgs,
                        content = "", flag = 0;
                    for(let i=0; i<msgs.length; i++){
                        if(msgs[i].hasRead !== flag){
                            content = content + "<div class='msg-item-separator'>———— 以下为更早消息 ————</div>";
                            flag = msgs[i].hasRead;
                        }
                        let createdDate = util.timeAgo(msgs[i].createdDate, false);
                        content = content +
                            "<div class='msg-item' data-msg-id='"+ msgs[i].id +"'>" +
                            "<img title='"+ msgs[i].fromUser.nickname +"' src='"+ msgs[i].fromUser.profilePath +"' data-user-id='"+ msgs[i].fromUser.userId +"'>" +
                            "<span>"+ msgs[i].content +"</span>" +
                            "<time>"+ createdDate +"</time>" +
                            "</div>";
                    }
                    if(flag === 0){
                        content = content + "<div class='msg-item-separator'>———— 以下为更早消息 ————</div>";
                    }
                    tabContent.innerHTML = content;

                    $.ajax({
                        type: "POST",
                        url: PRO_NAME + "/user/message/hasread",
                        data: {"type": msgType},
                        dataType: "json",
                        success: function (data) {
                            printRetMsg(data.code, data.msg);
                        },
                        async: true
                    });
                }else{
                    tabContent.innerHTML = "<div id='no-content-box'><span id='no-content-icon'><svg class='icon' aria-hidden='true'><use xlink:href='#icon-kong'></use></svg></span><span id='no-content'>空空如也，消息列表为空哦</span></div>";
                }
            },
            async: true
        });
    }

    //存储对话{对话ID：对话列表项}
    const CONVERSATIONS_WITHOUT_BLOCK = new Map();
    //存储{对话ID：对话窗口}
    const CONVERSATIONS = new Map();

    //获取所有相关用户
    function getUserList(){
        let plUser = document.querySelector("#pl-user");
        if(!isnull(plUser.value)){
            $.ajax({
                type: "GET",
                url: '/user/pluser',
                data: {
                    "nickname": plUser.value
                },
                dataType: "json",
                success:function(data){
                    let plUserOptional = document.querySelector("#pl-user-optional"),
                        content = "";
                    if(data.code === REQ_SUCC && data.data != null && data.data.users != null){
                        let users = data.data.users;
                        if(users.length > 0){
                            for(let i=0, len=users.length; i<len; i++){
                                content = content +
                                    "<span class='pluser-item' data-id='"+ users[i].conversationId +"' data-src='"+ users[i].profilePath +"' data-nickname='"+ users[i].nickname +"'>" +
                                    "<img src='"+ users[i].profilePath +"'>" + users[i].nickname +
                                    "</span>"
                            }
                        }else{
                            content = "<div id='no-pl-user'>查找不到相关用户</div>";
                        }
                        plUserOptional.innerHTML = content;
                        addEventToUser();       //顺序不可颠倒
                        plUserOptional.style.display = "block";
                    }
                },
                async: true
            });
        }
    }

    /**
     * 为查询到的用户添加点击事件
     */
    function addEventToUser(){
        let personalLetterBlock = document.querySelector("#personal-letter-items");
        let users = document.querySelectorAll(".pluser-item");
        for(let i=0, len=users.length; i<len; i++){
            users[i].addEventListener("click", function (e) {
                //判断会话是否已存在，存在则直接跳转到对应会话窗口；不存在则创建新窗口
                if(CONVERSATIONS_WITHOUT_BLOCK.get(this.getAttribute("data-id")) === undefined && CONVERSATIONS.get(this.getAttribute("data-id")) === undefined){
                    let child = document.createElement("span");
                    child.classList.add("personal-letter-item");
                    child.setAttribute("data-id", this.getAttribute("data-id"));
                    child.setAttribute("data-nickname", this.getAttribute("data-nickname"));
                    child.innerHTML = "<img src='"+ this.getAttribute("data-src") +"'>" +
                        "<span class='pluser-nickname'>"+ this.getAttribute("data-nickname") +"</span>" +
                        "<span class='pluser-msg'></span>" +
                        "<div class='conversation-unread-cnt'>0</div>";
                    personalLetterBlock.appendChild(child);
                    CONVERSATIONS_WITHOUT_BLOCK.set(child.getAttribute("data-id"), child);
                    child.addEventListener("click", function (e) {
                        selected(this);
                    });
                }else{
                    selected(CONVERSATIONS_WITHOUT_BLOCK.get(this.getAttribute("data-id")));
                }
            })
        }
    }

    /**
     * 获取对话信息
     */
    function getConversations() {
        $.ajax({
            type: "GET",
            url: '/user/conversations',
            dataType: "json",
            success:function(data){
                let personalLetterBlock = document.querySelector("#personal-letter-items"),
                    content = "";
                if(data.code === REQ_SUCC && data.data != null && data.data.conversations != null){
                    let conversations = data.data.conversations;
                    if(conversations.length > 0){
                        let unreadHtml = "";
                        for(let i=0, len=conversations.length; i<len; i++){
                            unreadHtml = conversations[i].unreadCnt > 0 ?
                                "<div class='conversation-unread-cnt' style='display: block'>"+ conversations[i].unreadCnt +"</div>" :
                                "<div class='conversation-unread-cnt'>"+ conversations[i].unreadCnt +"</div>";
                            content = content +
                                "<span class='personal-letter-item' data-id='"+ conversations[i].conversationId +"' data-nickname='"+ conversations[i].nickname +"'>" +
                                "<img src='"+ conversations[i].profilePath +"'>" +
                                "<span class='pluser-nickname'>"+ conversations[i].nickname +"</span>" +
                                "<span class='pluser-msg'>"+ conversations[i].message +"</span>" +
                                unreadHtml +
                                "</span>";
                        }
                    }
                    personalLetterBlock.innerHTML = content;
                    //为对话添加选中事件
                    let plusers = document.querySelectorAll(".personal-letter-item");
                    for(let i=0, len=plusers.length; i<len; i++){
                        CONVERSATIONS_WITHOUT_BLOCK.set(plusers[i].getAttribute("data-id"), plusers[i]);
                        plusers[i].addEventListener("click", function (e) {
                            selected(this);
                        });
                    }
                }
            },
            async: true
        });
    }
    //选中事件
    function selected(that) {
        let block = CONVERSATIONS.get(that.getAttribute("data-id"));
        changeSelected(that, "selected");       //改变对话为选中状态
        showMsgBlock(block, that);      //显示对话窗口
        updateMsgHasread(that.getAttribute("data-id"), that);       //标记消息为已读
    }
    //显示消息对话框
    function showMsgBlock(block, that) {
        if(block !== undefined){
            changeSelected(block, "show");
            let msgList = block.getElementsByClassName("msg-list")[0];
            msgList.scrollTop = msgList.scrollHeight;       //滚动到底部
        }else{
            block = addMsgBlock(that.getAttribute("data-id"), that.getAttribute("data-nickname"));
            CONVERSATIONS.set(that.getAttribute("data-id"), block);
            //定期查询该对话新消息
            setInterval(function () {
                let msgList = block.getElementsByClassName("msg-list")[0];
                let lastMsg = msgList.lastElementChild;
                $.ajax({
                    type: "GET",
                    url: '/user/message/get/regular',
                    data: {
                        "conversationId": that.getAttribute("data-id"),
                        "lastTime": lastMsg == null ? null : lastMsg.getAttribute("data-time")
                    },
                    dataType: "json",
                    success:function(data){
                        if(data.code === REQ_SUCC && data.data != null && data.data.messages != null){
                            let lastMsgContent = "";
                            let lis = [], newMsgCnt = 0;
                            let messages = data.data.messages;
                            for(let i=messages.length-1; i>=0; i--){
                                let item = messages[i];
                                let createTime = util.timeAgo(item.createdDate, false);
                                if(item.self){
                                    lis.push("<li data-id='"+ item.id +"' data-time='"+ item.createdDate +"'>" +
                                        "<div class='right'><div class='message-box'><span class='content'>"+ item.content +"</span>" +
                                        "<span class='other'><a data-id='"+ item.id +"'></a><time>"+ createTime +"</time></span></div>" +
                                        "<img src='"+ item.fromUser.profilePath +"'>" +
                                        "</div></li>");
                                }else{
                                    lis.push("<li data-id='"+ item.id +"' data-time='"+ item.createdDate +"'>" +
                                        "<div class='left'><img src='"+ item.fromUser.profilePath +"'>" +
                                        "<div class='message-box'><span class='content'>"+ item.content +"</span>" +
                                        "<span class='other'><time>"+ createTime +"</time><a data-id='"+ item.id +"'></a></span></div>" +
                                        "</div></li>");
                                    newMsgCnt++;
                                }
                                lastMsgContent = item.content;  //最后的消息内容
                            }
                            msgList.innerHTML = msgList.innerHTML + lis.join('');
                            if(lis.length > 0){
                                msgList.scrollTop = msgList.scrollHeight; //滚动到底部
                                that.getElementsByClassName("pluser-msg")[0].innerHTML = lastMsgContent;  //更新最新消息
                                //更新未读消息数
                                if(newMsgCnt > 0){
                                    let unreadCnt = that.getElementsByClassName("conversation-unread-cnt")[0];
                                    unreadCnt.innerHTML = parseInt(unreadCnt.innerText) + newMsgCnt;
                                    unreadCnt.style.display = "block";
                                }
                            }
                        }
                    },
                    async: true
                });
            }, 2000);
        }
    }

    //更新会话消息已读
    function updateMsgHasread(conversationId, pluser) {
        let unreadCnt = pluser.getElementsByClassName("conversation-unread-cnt")[0];
        unreadCnt.innerHTML = "0";
        unreadCnt.style.display = "none";
        $.ajax({
            type: "POST",
            url: '/user/personalmessage/hasread',
            data: {
                "conversationId": conversationId
            },
            dataType: "json",
            success:function(data){

            },
            async: true
        });
    }

    //更改对话选中项
    function changeSelected(that, cla){
        that.classList.add(cla);
        let sibings = siblings(that);
        for(let j=0, len1=sibings.length; j<len1; j++){
            sibings[j].classList.remove(cla);
        }
    }

    //创建对话框
    let editBoxCnt = 0;
    function addMsgBlock(conversationId, nickname){
        let rightside = document.querySelector("#rightside");
        let editId = "edit-" + conversationId;
        let listId = "list-" + conversationId;
        let sendBtnId = "send-msg-" + conversationId;
        let child = document.createElement("div");
        child.classList.add("msg-block");
        child.innerHTML = "<div class='msg-block-title'>与<em>"+ nickname +"</em>的对话</div><div id='"+ listId +"' class='msg-list'></div><div class='edit-block'><textarea id='"+ editId +"' style='display: none;'></textarea><span class='send-msg-progress' id='send-msg-progress-"+ conversationId +"'></span><span id='"+ sendBtnId +"' class='send-msg'>发送消息</span></div>";
        rightside.appendChild(child);
        /*==========上滚加载==========*/
        let limit = 6,      //存储当前消息分页信息
            scrollFlag = true;      //是否触发滚动事件
        let listScroll = document.querySelector("#" + listId);
        getMessageDetail(listScroll, conversationId);
        let p = 0, t = 0;       //判断上滚还是下滚
        listScroll.addEventListener("scroll", function (e) {
            p = $(this).scrollTop();
            if(t<=p){   //下滚
            }else{  //上滚
                if(scrollFlag && listScroll.scrollTop < 80){
                    scrollFlag = false;
                    getMessageDetail(listScroll, conversationId);
                }
            }
            setTimeout(function(){t = p;},0);
        });

        //查询消息
        function getMessageDetail(scrollBlock, conversationId){
            let firstMsg = scrollBlock.firstElementChild;       //获取最早消息和添加加载动画这两个操作不能颠倒顺序
            let loadEleId = msgPartLoading(scrollBlock, createRandomId());
            $.ajax({
                type: "GET",
                url: PRO_NAME + "/user/message/get",
                data: {
                    "conversationId": conversationId,
                    "limit": limit,
                    "firstTime": firstMsg == null ? null : firstMsg.getAttribute("data-time")
                },
                dataType: "json",
                success: function (data) {
                    if(data.code === REQ_SUCC && data.data != null){
                        let isFirstLoad = false;  //判断是否首次加载对话信息
                        let lis = [];  //存放查找到的数据
                        if(data.data.messages != null){
                            let first = firstMsg == null ? null : firstMsg.getAttribute("data-id");
                            let messages = data.data.messages;
                            for(let i=messages.length-1; i>=0; i--){
                                let item = messages[i];
                                let createTime = util.timeAgo(item.createdDate, false);
                                if(item.self){
                                    lis.push("<li data-id='"+ item.id +"' data-time='"+ item.createdDate +"'>" +
                                        "<div class='right'><div class='message-box'><span class='content'>"+ item.content +"</span>" +
                                        "<span class='other'><a data-id='"+ item.id +"'></a><time>"+ createTime +"</time></span></div>" +
                                        "<img src='"+ item.fromUser.profilePath +"'>" +
                                        "</div></li>");
                                }else{
                                    lis.push("<li data-id='"+ item.id +"' data-time='"+ item.createdDate +"'>" +
                                        "<div class='left'><img src='"+ item.fromUser.profilePath +"'>" +
                                        "<div class='message-box'><span class='content'>"+ item.content +"</span>" +
                                        "<span class='other'><time>"+ createTime +"</time><a data-id='"+ item.id +"'></a></span></div>" +
                                        "</div></li>");
                                }
                            }
                            scrollBlock.innerHTML = lis.join('') + scrollBlock.innerHTML;
                            if(first == null){
                                isFirstLoad = true;
                                scrollBlock.scrollTop = scrollBlock.scrollHeight; //滚动到底部
                            }else{
                                isFirstLoad = false;
                            }
                            scrollFlag = true;  //可继续上滚加载消息
                        }else{
                            scrollFlag = false;  //已经没有旧消息了，禁止加载
                            let banToLoad = document.createElement("div");
                            banToLoad.classList.add("ban-to-load");
                            banToLoad.innerHTML = "已经到达天花板了 ㄟ(≧◇≦)ㄏ";
                            scrollBlock.insertBefore(banToLoad, scrollBlock.firstElementChild);
                        }
                        if(!isFirstLoad && lis.length > 0){
                            scrollBlock.scrollTop = lis.length*75;
                            //$("body,html").animate({scrollTop: 0}, 300);
                        }
                    }
                },
                complete: function(){
                    loaded(scrollBlock, document.getElementById(loadEleId));  //移除加载动画
                },
                async: true
            });
        }
        let index = layedit.build(editId, {
            height: 105,
            tool: ['face']
        });
        editBoxCnt++;       //统计消息输入框数量，供JS获取输入框时使用
        changeSelected(child, "show");      //改变对话为选中状态
        //为输入区绑定事件
        let sendBtn = document.querySelector("#" + sendBtnId),
            sendMsgProgress = document.querySelector("#send-msg-progress-"+conversationId),
            isClickSendBtn = true;
        //监听输入框输入事件
        window.frames["LAY_layedit_"+editBoxCnt].document.getElementsByTagName("body")[0].addEventListener('focus', function () {
            sendMsgProgress.innerHTML = "输入中 ( • ̀ω•́ )";
        });
        window.frames["LAY_layedit_"+editBoxCnt].document.getElementsByTagName("body")[0].addEventListener('blur', function () {
            layedit.sync(index);
            let content = document.querySelector("#"+editId).value;
            if(isNullStr(content)){
                sendMsgProgress.innerHTML = "";
                return;
            }
            sendMsgProgress.innerHTML = "待发送 ( • ̀ω•́ )";
        });
        //监听“发送消息”按钮点击事件
        sendBtn.addEventListener("click", function (e) {
            if(isClickSendBtn){
                isClickSendBtn = false;
                setTimeout(function () {
                    isClickSendBtn = true;
                }, 1500);
                layedit.sync(index);
                let content = document.querySelector("#"+editId).value;
                if(isNullStr(content)){
                    layer.msg("发送消息不能为空", {
                        time: 2500
                    });
                    return;
                }
                sendMsgProgress.innerHTML = "发送中 ( • ̀ω•́ )";
                $.ajax({
                    type: "POST",
                    url: '/user/message/send',
                    data: {
                        "conversationId": conversationId,
                        "content": content
                    },
                    dataType: "json",
                    success:function(data){
                        if(data.code === REQ_SUCC){
                            sendMsgProgress.innerHTML = "已发送 ヽ(*・ω・)ﾉ";
                            layedit.setContent(index, "", false);
                        }else{
                            sendMsgProgress.innerHTML = "发送失败 (ﾒﾟДﾟ)ﾒ";
                            layer.msg(data.code + ":" + data.msg, {
                                time: 2500
                            });
                        }
                    },
                    async: true
                });
            }
        });

        return child;
    }

    //输入框中根据用户输入内容动态查询
    let clocker = null;
    function innerKeydown() {
        if(null == clocker) {
            clocker = setTimeout(getUserList,700);
        } else {
            clearTimeout(clocker);
            clocker = setTimeout(getUserList,700);
        }
    }

    function hideUsersDiv(){
        let plUserOptional = document.querySelector("#pl-user-optional");
        plUserOptional.innerHTML = "";
        plUserOptional.style.display = "none";
    }

    function clearInput(){
        let plUser = document.querySelector("#pl-user");
        plUser.value = "";
    }
});