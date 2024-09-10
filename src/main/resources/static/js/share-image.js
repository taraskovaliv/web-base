let shareButtons = document.getElementsByClassName("share-image");
[].forEach.call(shareButtons, function (el) {
    el.addEventListener('click', async () => {
        // Function to convert base64 to blob
        function base64ToBlob(base64, type = 'image/png') {
            const byteCharacters = atob(base64.split(',')[1]);
            const byteNumbers = new Array(byteCharacters.length);
            for (let i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            const byteArray = new Uint8Array(byteNumbers);
            return new Blob([byteArray], {type: type});
        }

        if (navigator.share) {
            try {
                const blob = base64ToBlob(el.attributes['img'].value);
                const file = new File([blob],
                    el.attributes['img-name'].value + '.png',
                    {type: 'image/png'}
                );

                await navigator.share({
                    files: [file],
                    title: 'Check out this image!',
                    text: 'Here\'s a simple image I wanted to share with you.'
                });
            } catch (error) {
                alert('Error sharing the image. Check the console for details.');
            }
        } else {
            alert('Sorry, your browser doesn\'t support image sharing.');
        }
    });
});