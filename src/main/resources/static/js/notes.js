layui.use(['layer', 'element', 'util'], function () {
    let layer = layui.layer,
        element = layui.element,
        util = layui.util;

    listUserNotesByStatusAjax("#published", 1, "3");
    element.on('tab(mynotes-tab)', function (data) {
        let tab = document.querySelectorAll(".layui-this")[0];
        let tabId = tab.getAttribute("data-status");
        let contentId, status;
        if(tabId === "1"){
            contentId = "#recycle-bin";
            status = 0;
        }else if(tabId === "2"){
            contentId = "#saved";
            status = 2;
        }else{
            contentId = "#published";
            status = 1;
        }
        listUserNotesByStatusAjax(contentId, status, tabId);
    });
    //Ajax获取对应的手记
    function listUserNotesByStatusAjax(contentId, status, tabId) {
        let content = document.querySelector(contentId);
        $.ajax({
            type: "GET",
            url: PRO_NAME + "/user/mynotes/"+status,
            dataType: "json",
            success: function (data) {
                if(data.code === REQ_SUCC && data.data !== undefined && data.data.notes.length > 0){
                    let notes = data.data.notes;
                    let innerhtml = "";
                    let noteUrl = "", readDiv = "";
                    for (let i = 0; i < notes.length; i++) {
                        if(status === 1 || status === 0){
                            noteUrl = "/note/";
                        }else{
                            noteUrl = "/user/note/edit/";
                        }
                        if(status === 1){
                            readDiv = "<span><a>浏览量</a>"+ notes[i].readCnt +"</span>";
                        }else{
                            readDiv = "";
                        }
                        let createTime = util.toDateString(notes[i].createTime, 'yyyy-MM-dd HH:mm:ss');
                        innerhtml = innerhtml +
                            "<article class='article'>" +
                            "<h2><a class='noteTitle' href='"+ PRO_NAME + noteUrl + notes[i].id +"' title='"+ notes[i].title +"'>"+ notes[i].title +"</a>" + noteFunc(tabId, notes[i].id) +
                            "<p>点击进入详细阅读</p>" +
                            "<div class='articleInfo'>" +
                            "<span class='content-type'>"+ notes[i].type +"</span>" +
                            "<time>"+ createTime +"</time>" +
                            "<div class='readInfo'>" + readDiv +
                            "<span><a>评论</a>"+ notes[i].commentCnt +"</span>" +
                            "</div>" +
                            "</div>" +
                            "</article>";
                    }
                    content.innerHTML = innerhtml;
                    addNoteFunc(contentId, status, tabId);
                }else{
                    content.innerHTML = "<div id='no-content-box'><span id='no-content-icon'><svg class='icon' aria-hidden='true'><use xlink:href='#icon-kong'></use></svg></span><span id='no-content'>空空如也，没有相关内容哦</span></div>";
                }
            },
            async: true
        });
    }

    //根据不同的tabId获得不同的功能项
    function noteFunc(tabId, noteId) {
        let s = "";
        if(tabId === "1"){
            s = "<a class='restore-note' data-noteId='"+ noteId +"'>还原</a><a class='completely-delete-note' data-noteId='"+ noteId +"'>彻底删除</a></h2>";
        }else if(tabId === "2"){
            s = "<a class='edit-saved-note' href='/user/note/edit/"+noteId+"' data-noteId='"+ noteId +"'>编辑</a><a class='delete-saved-note' data-noteId='"+ noteId +"'>删除</a></h2>";
        }else{
            s = "<a class='edit-saved-note' href='/user/note/edit/"+noteId+"' data-noteId='"+ noteId +"'>编辑</a><a class='delete-note' data-noteId='"+ noteId +"'>删除</a></h2>";
        }

        return s;
    }
    //为手记添加事件
    function addNoteFunc(contentId, status, tabId) {
        if(tabId === "1"){
            completelyDeleteNote(contentId, status, tabId);
            restoreNote(contentId, status, tabId);
        }else if(tabId === "2"){
            deleteSavedNote(contentId, status, tabId);
        }else{
            deletePublishedNote(contentId, status, tabId);
        }
    }
    //删除已发布手记
    function deletePublishedNote(contentId, status, tabId) {
        updateNoteStatus("delete-note", "确认删除该手记？（进入回收站）", "/user/note/delete", "手记已删除", "手记删除失败", contentId, status, tabId);
    }
    //删除已保存手记
    function deleteSavedNote(contentId, status, tabId){
        updateNoteStatus("delete-saved-note", "确认彻底删除该手记？（不会进入回收站）", "/user/note/deletesaved", "手记已彻底删除", "手记删除失败", contentId, status, tabId);
    }
    //还原手记
    function restoreNote(contentId, status, tabId) {
        updateNoteStatus("restore-note", "还原该手记？(还原为发布状态)", "/user/note/restore", "手记已还原", "手记还原失败", contentId, status, tabId);
    }
    //彻底删除手记
    function completelyDeleteNote(contentId, status, tabId) {
        updateNoteStatus("completely-delete-note", "确认彻底删除该手记？（不可恢复）", "/user/note/deletecompletely", "手记已彻底删除", "手记删除失败", contentId, status, tabId);
    }

    /**
     * 更新手记状态
     * @param claName 要监听事件的按钮类名
     * @param confirmMsg 询问信息
     * @param url 请求url
     * @param retMsg1 操作成功显示信息
     * @param retMsg2 操作失败显示信息
     * @param contentId tab对应内容区域的id
     * @param status 手记的原状态
     * @param tabId tab对应的索引
     */
    function updateNoteStatus(claName, confirmMsg, url, retMsg1, retMsg2, contentId, status, tabId) {
        let btns = document.querySelectorAll("."+claName);
        for(let i=0; i<btns.length; i++){
            btns[i].addEventListener("click", function () {
                layer.confirm(
                    confirmMsg,
                    {
                        btnAlign: 'c',
                        title: false
                    },
                    function (index) {
                        let noteId = btns[i].getAttribute("data-noteId");
                        layer.close(index);
                        $.ajax({
                            type: "POST",
                            url: PRO_NAME + url,
                            data: {"noteId": noteId},
                            dataType: "json",
                            success: function (data) {
                                if(data.code === REQ_SUCC){
                                    layer.msg(retMsg1, {
                                        time: 2500
                                    });
                                    listUserNotesByStatusAjax(contentId, status, tabId);
                                }else{
                                    layer.msg(retMsg2, {
                                        time: 2500
                                    });
                                }
                            },
                            async: true
                        });
                    },
                    function (index) {
                        layer.close(index);
                    }
                );
            })
        }
    }
});