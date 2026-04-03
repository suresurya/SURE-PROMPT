// Form initialization
$(document).ready(function() {
    if ($('#tags').length) {
        $('#tags').select2({
            placeholder: "Select up to 5 topic tags...",
            maximumSelectionLength: 5,
            width: '100%'
        });
    }
});
