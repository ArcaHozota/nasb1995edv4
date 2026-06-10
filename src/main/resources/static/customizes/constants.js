const responseSuccess = 'SUCCESS';
const responseFailure = 'FAILURE';
const emptyString = '';
const inputWarning = '入力情報不正';
const inputString = '追加済み';
const delimiter = ' / ';
const delayApology = 'すみませんが、当機能はまだ実装されておりません';
const showVadMsgError = '名称を空になってはいけません。';
const GET = 'GET';
const POST = 'POST';
const PUT = 'PUT';
const DELETE = 'DELETE';
const trimQuote = (str) =>
    typeof str === 'string'
        ? str.replace(/^"|"$/g, emptyString)
        : emptyString;
// 【修正版】UTF-8 文字列を Base64 に
const utf8ToBase64 = (str) => {
    return encodeURIComponent(
        btoa(unescape(encodeURIComponent(str)))
    );
};

// 【修正版】Base64 を UTF-8 文字列に
const base64ToUtf8 = (str) => {
    return decodeURIComponent(escape(atob(str)));
};

function buildPageInfos(response) {
    const pageInfos = document.getElementById("pageInfos");
    pageInfos.innerHTML = emptyString;
    pageNum = response.pageNum;
    totalPages = response.totalPages;
    totalRecords = response.totalRecords;
    pageInfos.textContent = `${totalPages}ページ中の${pageNum}ページ、${totalRecords}件のレコードが見つかりました。`;
}

function buildPageNavi(result) {
    const pageNavi = document.getElementById("pageNavi");
    pageNavi.innerHTML = emptyString;
    const ul = document.createElement('ul');
    ul.classList.add('pagination');
    const createPageItem = (label, disabled, clickHandler) => {
        const li = document.createElement('li');
        li.className = 'page-item';
        const a = document.createElement('a');
        a.className = 'page-link';
        a.href = '#';
        a.innerHTML = label;
        if (disabled) {
            li.classList.add('disabled');
        } else if (clickHandler) {
            li.addEventListener('click', clickHandler);
        }
        li.appendChild(a);
        return li;
    };
    ul.appendChild(createPageItem("&laquo;", !result.hasPrevPage, () => toSelectedPg(1, keyword)));
    ul.appendChild(createPageItem("&lsaquo;", !result.hasPrevPage, () => toSelectedPg(pageNum - 1, keyword)));
    result.navigateNos.forEach(item => {
        const li = document.createElement('li');
        li.className = 'page-item';
        const a = document.createElement('a');
        a.className = 'page-link';
        a.href = '#';
        a.textContent = item;
        if (pageNum === item) {
            li.classList.add('active');
        }
        li.appendChild(a);
        li.addEventListener('click', () => toSelectedPg(item, keyword));
        ul.appendChild(li);
    });
    ul.appendChild(createPageItem("&rsaquo;", !result.hasNextPage, () => toSelectedPg(pageNum + 1, keyword)));
    ul.appendChild(createPageItem("&raquo;", !result.hasNextPage, () => toSelectedPg(totalPages, keyword)));
    const nav = document.createElement('nav');
    nav.appendChild(ul);
    pageNavi.appendChild(nav);
}
