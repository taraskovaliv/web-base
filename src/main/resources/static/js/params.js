function addUrlParameter(name, value) {
    const searchParams = new URLSearchParams(window.location.search);
    searchParams.set(name, value)
    window.location.search = searchParams.toString()
}

function addAnchor(anchor) {
    window.location.hash = anchor.toString()
}