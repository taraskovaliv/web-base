const els_add = document.getElementsByClassName('popup-open');
[].forEach.call(els_add, function (el) {
    el.addEventListener("click",
        function (e) {
            e.preventDefault();
            document.getElementById(el.attributes['popup-target'].value)
                .classList.add("show");
        });
});

const els_close = document.getElementsByClassName('popup-close');
[].forEach.call(els_close, function (el) {
    el.addEventListener("click",
        function (e) {
            e.preventDefault();
            document.getElementById(el.attributes['popup-target'].value)
                .classList.remove("show");
        });
});

window.addEventListener(
    "click",
    function (e) {
        if (e.target.classList.contains("show")) {
            e.target.classList.remove("show");
        }
    }
);