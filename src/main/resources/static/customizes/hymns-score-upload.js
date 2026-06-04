const pageNum = document.getElementById("pageNumContainer")?.value;
const keyword = document.getElementById("keywordContainer")?.value;

document.addEventListener("DOMContentLoaded", () => {
    const toCollection = document.getElementById("toCollection");
    if (toCollection) {
        toCollection.classList.remove('text-white');
        toCollection.classList.add('active');
    }
});

document.getElementById("toHymnPages")?.addEventListener("click", (e) => {
    e.preventDefault();
    const url = '/hymns/to-pages?pageNum=' + pageNum + '&keyword=' + keyword;
    checkPermissionAndTransfer(url);
});

document.getElementById("scoreUploadBtn")?.addEventListener("click", () => {
    const inputSelectors = ["#scoreEdit"];
    inputSelectors.forEach(sel => {
        const el = document.querySelector(sel);
        el.classList.remove("is-valid", "is-invalid");
        const span = el.nextElementSibling;
        if (span?.tagName === "SPAN") {
            span.classList.remove("valid-feedback", "invalid-feedback");
            span.textContent = emptyString;
        }
    });
    const listArray = projectInputContextGet(inputSelectors);
    if (listArray.includes(emptyString)) {
        projectNullInputBoxDiscern(inputSelectors);
        return;
    }
    const editId = document.getElementById("idContainer")?.value;
    const fileInput = document.getElementById("scoreEdit");
    const file = fileInput?.files[0];
    if (!file) {
        return;
    }
    upload(editId, file);
});

async function upload(editId, file) {
    const header = document.querySelector('meta[name="_csrf_header"]')?.content;
    const token = document.querySelector('meta[name="_csrf_token"]')?.content;
    const formData = new FormData();
    formData.append("id", editId);
    formData.append("score", file);
    try {
        const res = await fetch('/hymns/score-upload', {
            method: 'POST',
            headers: {
                ...(header && token ? { [header]: token } : {})
            },
            body: formData
        });
        const text = await res.text();
        if (!res.ok) {
            layer.msg(text);
            return;
        }
        localStorage.setItem('redirectMessage', text);
        window.location.replace(
            '/hymns/to-pages?pageNum=' + encodeURIComponent(pageNum)
            + '&keyword=' + encodeURIComponent(keyword)
        );
    } catch (e) {
        console.error(e);
        layer.msg("通信エラーが発生しました。");
    }
}