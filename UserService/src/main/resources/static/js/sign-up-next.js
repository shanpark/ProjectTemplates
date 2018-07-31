$(document).ready(function() {
    $("#birth").datepicker({
        changeMonth: true,
        changeYear: true,
        yearRange: "c-100:+0",
        dateFormat: "yy/mm/dd",
        onSelect: function() {
            $(this).trigger('input'); // for .ani-input-ctrl
        }
    });
});