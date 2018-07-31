$(document).ready(function() {
    function updateClass(obj) {
        if (obj.val())
            obj.closest(".ani-input-group").addClass('not-empty');
        else
            obj.closest(".ani-input-group").removeClass('not-empty');
    }

    $('.ani-input-ctrl').each(function(index, element) {
        updateClass($(element));
        $(element).on('input', function(events) {
            updateClass($(this));
        });
    });
});
