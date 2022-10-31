
function addPage($page, $query) {
            
    url = new URL(window.location.href.split('?')[0]);
    location.href = url + "?q="+$query + "&page="+($page+1);
}

function goBack($page, $query) {
    url = new URL(window.location.href.split('?')[0]);
    location.href = url + "?q="+$query + "&page="+($page-1);
}

function getImage($src, $title) {
    var div = document.getElementById("searchImgTh");
    
    div.innerHTML = '<img class="imgShow" src="%img%" ><div style="color:white;"class="serp__title">%title%</div><a href="%img%" target="_blank"><div class="serp__url">%img%</div></a>'.replace("%img%", $src).replace("%title%", $title).replace("%img%", $src).replace("%img%", $src);
    if(div.classList.contains("hidden")) {
        div.classList.remove("hidden");
    }
}