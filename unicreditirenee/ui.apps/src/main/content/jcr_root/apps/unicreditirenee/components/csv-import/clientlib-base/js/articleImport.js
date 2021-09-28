$(document).ready(function () {
  $("#csv").change(function () {
    $("#selectedFile").text(
      $("#csv")
        .val()
        .replace(/C:\\fakepath\\/i, "")
    );
  });

  $("#btnSubmit").click(function (event) {
    if ($("#csv").val().length > 1) {
      var filename = $("#csv").val();

      var extension = filename.replace(/^.*\./, "");

      if (extension == filename) {
        extension = "";
      } else {
        extension = extension.toLowerCase();
      }

      if (extension != "csv") {
        alert("Only CSV format is allowed!");
        event.preventDefault();
        return;
      }

      event.preventDefault();

      var data = new FormData();

      data.append("file", $("input[type=file]")[0].files[0]);

      console.log(data);

      $("#btnSubmit").prop("disabled", true);

      $(".report").removeClass("btn--show").addClass("btn--hide");
      $(".loading").removeClass("loading--hide").addClass("loading--show");
      $("#btnSubmit").addClass("btn--hide");
      $(".result label").hide();

      $.ajax({
        type: "post",
        enctype: "multipart/form-data",
        url: "/bin/articles/import",
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        success: function (res) {
          CheckProgress();
        },
        error: function (e) {
          console.log(e.responseText);
          $(".result label").text("Something went wrong");
          $(".result label").show();
          $(".loading").removeClass("loading--show").addClass("loading--hide");
          $("#btnSubmit").prop("disabled", false);
        },
      });
    } else {
      alert("Please, Select a CSV file to upload");
      event.preventDefault();
      return;
    }
  });
});

// Progress check start
function CheckProgress() {
  $.ajax({
    url: "/bin/import/status",
    type: "GET",
    dataType: "json",
    contentType: "application/json",
    success: function (data) {
      var processedRowsPercent = 0;
      processedRowsPercent = data.processedRows / data.csvRows;
      processedRowsPercent =
        data.processedRows == 0 ? 0 : processedRowsPercent * 100;

      $(".processedRowsPercent label").text(
        "Percentage " + processedRowsPercent + "%"
      );
      $(".processedRowsPercent label").show();

      if (data.state == "completed") {
        setTimeout(() => {
          $(".report").removeClass("btn--hide").addClass("btn--show");
          $(".result label").text("Process Status:");
          $(".result label").show();
          $(".csvRows label").text("csvRows: " + data.csvRows);
          $(".csvRows label").show();

          $(".processedRows label").text(
            "processedRows: " + data.processedRows
          );
          $(".processedRows label").show();

          $(".createdRows label").text("createdRows: " + data.createdRows);
          $(".createdRows label").show();

          $(".skippedRows label").text("skippedRows: " + data.skippedRows);
          $(".skippedRows label").show();

          $(".processedRowsPercent label").text(
            "Percentage " + processedRowsPercent + "%"
          );
          $(".processedRowsPercent label").show();

          $(".loading").removeClass("loading--show").addClass("loading--hide");
          $("#btnSubmit").removeClass("btn--hide").addClass("btn--show");
          $("#btnSubmit").prop("disabled", false);
        }, 1000);
      } else if (data.state == "processing") {
        processedRowsPercent = processedRowsPercent.toFixed(2);

        $(".processedRowsPercent label").text(
          "Percentage " + processedRowsPercent + "%"
        );
        $(".processedRowsPercent label").show();

        var progress =
          "<progress id='progress' class='progress' value=" +
          processedRowsPercent +
          "max='100'></progress>";
        $("#progressBar").html(progress);
        setTimeout(function () {
          CheckProgress();
        }, 1000);
      }
    },
  });
}
// Progress check ended
