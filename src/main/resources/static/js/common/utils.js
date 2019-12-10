const PRO_NAME = "";
/**
 * 状态码
 */
const REQ_SUCC = 0;
const PARAM_FAIL = 1001;
const REQ_SEREOR = 1002;

/**
 * 实体类型
 */
const ENTITY_NOTE = 1;
const ENTITY_COMMENT = 2;
const ENTITY_USER = 3;
const ENTITY_FEED = 4;

const TAKEIN = "&#10;";

/**
 * 根据类型值获取手记类型
 */
function getTypeName(type){
    const map = {
        1: "前端开发",
        2: "后台开发",
        3: "移动端",
        4: "IOS",
        5: "大数据",
        6: "算法",
        7: "小程序",
        8: "运维"
    };

    return map[type];
}

function printRetMsg(code, msg) {
    console.log(code + ":" + msg);
}

function isnull(val) {
    var str = val.replace(/(^\s*)|(\s*$)/g, '');//去除空格;

    if ((typeof str != "undefined") && (typeof str.valueOf() == "string") && (str.length > 0)) {
        return false;
    } else {
        return true;
    }
}

function isNullStr(str) {
    if((typeof str != "undefined") && (typeof str.valueOf() == "string") && (str.length > 0)){
        return false;
    }
    return true;
}

/**
 * 判断某元素是否属于某个类
 * @param cla 类名
 * @param element 元素
 * @return boolean true表示属于，false表示不属于
 */
function hasClass(cla, element) {
    if (element.className.trim().length === 0) return false;
    let allClass = element.className.trim().split(" ");
    return allClass.indexOf(cla) > -1;
}

/**
 * 为元素添加类
 * @param cla 类名
 * @param element 元素
 */
function addClass(cla, element) {
    if (!hasClass(cla, element)) {
        if (element.setAttribute) {
            element.setAttribute("class", element.getAttribute("class") + " " + cla);
        } else {
            element.className = element.className + " " + cla;
        }
    }
}

/**
 * 生成唯一Id
 * @return {string}
 */
function createRandomId() {
    return (Math.random()*10000000).toString(16).substr(0,4)+'-'+(new Date()).getTime()+'-'+Math.random().toString().substr(2,5);
}

/**
 * 添加加载动画
 * @param element 需要添加加载动画的元素
 */
function fullScreenLoading(element, id) {
    let loadElement = document.createElement("div");
    loadElement.id = id;
    addClass("lds-css-full-screen", loadElement);
    addClass("ng-scope", loadElement);
    loadElement.innerHTML = "<div style='width:100%;height:100%;' class='lds-double-ring'>" +
        "<div></div>" +
        "<div></div>" +
        "</div>";
    element.appendChild(loadElement);

    return loadElement.id;
}

/**
 * 添加加载动画
 * @param element 需要添加加载动画的元素
 */
function partLoading(element, id) {
    let loadElement = document.createElement("div");
    loadElement.id = id;
    addClass("lds-css-part", loadElement);
    addClass("ng-scope", loadElement);
    loadElement.innerHTML = "<div style='width:100%;height:100%;' class='lds-double-ring'>" +
        "<div></div>" +
        "<div></div>" +
        "</div>";
    element.appendChild(loadElement);

    return loadElement.id;
}

/**
 * 添加加载动画
 * @param element 需要添加加载动画的元素
 */
function msgPartLoading(element, id) {
    let loadElement = document.createElement("div");
    loadElement.id = id;
    addClass("lds-css-part-msg", loadElement);
    addClass("ng-scope", loadElement);
    loadElement.innerHTML = "<div style='width:100%;height:100%;' class='lds-double-ring'>" +
        "<div></div>" +
        "<div></div>" +
        "</div>";
    element.insertBefore(loadElement, element.firstElementChild);

    return loadElement.id;
}

/**
 * 移除加载动画
 * @param element 需要移除加载动画的元素
 * @param child
 */
function loaded(element, child) {
    if(element && child && element.contains(child)){
        element.removeChild(child);
    }
}

/**
 * 判断是否已登录
 * @return {boolean}
 */
function hasLogin() {
    let isLogin = false;
    $.ajax({
        type: "GET",
        url: PRO_NAME + "/haslogin",
        dataType: "json",
        success: function (data) {
            if (data.data.status === 1) {
                printRetMsg(0, data.msg);
                isLogin = true;
            } else {
                printRetMsg(0, data.msg);
                isLogin = false;
            }
        },
        async: false
    });

    return isLogin;
}

/**
 * 弹窗显示需要登录
 */
function showNeedLogin(layer, content, next) {
    layui.layer.alert(content, {
        title: false,
        shadeClose: true,
        btn: '<span style="font-size: 14px;">登录 or 注册</span>',
        yes: function () {
            window.location.href = PRO_NAME + "/relogin?next=" + next;
        }
    });
}

/**
 * 获取相对路径
 * @return {string}
 */
function getUrlRelativePath(){
    let url = document.location.toString();
    let arrUrl = url.split("//");

    let start = arrUrl[1].indexOf("/");
    let relUrl = arrUrl[1].substring(start);//stop省略，截取从start开始到结尾的所有字符

    if(relUrl.indexOf("?") != -1){
        relUrl = relUrl.split("?")[0];
    }
    return relUrl;
}

/**
 * 创建居中新窗口
 * @param url 新窗口链接
 * @param name 新窗口名字
 * @param iWidth 新窗口宽度
 * @param iHeight 新窗口高度
 */
function openwindow(url,name,iWidth,iHeight) {
    let iTop = (window.screen.availHeight-30-iHeight)/2;       //获得窗口的垂直位置;
    let iLeft = (window.screen.availWidth-10-iWidth)/2;           //获得窗口的水平位置;
    window.open(
        url,
        name,
        'height='+iHeight+',innerHeight='+iHeight+',width='+iWidth+',innerWidth='+iWidth+',top='+iTop+',left='+iLeft+',menubar=0,scrollbars=1,resizable=1,status=1,titlebar=0,toolbar=0,location=1');
}

/**
 * 获取元素所有兄弟节点
 * @param elm
 * @return {Array}
 */
function siblings(elm) {
    var a = [];
    var p = elm.parentNode.children;
    for ( var i =0,pl= p.length;i<pl;i++) {
        if (p[i] !== elm) a.push(p[i]);
    }
    return a;
}

/**
 * 判断是否为评论者本人
 * @return {boolean}
 */
function isCommentOwner(commentId){
    let isOwner = false;
    $.ajax({
        type: "GET",
        url: PRO_NAME + "/comment/owner",
        data: {
            "commentId": commentId
        },
        dataType: "json",
        success: function (data) {
            if(data.code === REQ_SUCC){
                if(data.data !== undefined){
                    if(data.data.status === 1){
                        isOwner = true;
                    }
                }
            }
        },
        async: false
    });

    return isOwner;
}