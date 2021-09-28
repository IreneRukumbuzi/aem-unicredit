/* PHOTOGALLERY
============================= */

$(document).ready(function () {
  var windowPhoto = $(window).width();

  pictureSlider();

  // Expand Click visiblity
  $(".photogallery .gallery-top .photogallery__expand").click(function () {
    $("body").css("height", "100vh");
    $("body").css("overflow", "hidden");
    //TYPE 1 Mobile Modal
    if ($(window).width() <= 1024) {
      $(".photogallery .gallery-top .photogallery__expand .icon")
        .removeClass("icon-expand")
        .addClass("icon-close");
      if ($(".photogallery .gallery-top").hasClass("gallery__modal--mobile")) {
        $(".photogallery .gallery-top")
          .removeClass("gallery__modal--mobile")
          .css("height", "");

        $(".photogallery .gallery-top .photogallery__pagination").removeClass(
          "gallery__modal-paginator--mobile"
        );

        $(".photogallery .gallery-top .photogallery__expand .icon")
          .addClass("icon-expand")
          .removeClass("icon-close");
        $("body").css("height", "100%");
        $("body").css("overflow", "");
      } else {
        $(".photogallery .gallery-top")
          .addClass("gallery__modal--mobile")
          .css("height", window.innerHeight);
        $(".photogallery .gallery-top .photogallery__pagination").addClass(
          "gallery__modal-paginator--mobile"
        );
      }
      //TYPE 2 Desk Modal
    } else {
      $("body").css("overflow", "hidden");
      $(".photogallery__modal").fadeIn();
      updateModalDesk();
    }
  });

  // Desktop Modal Arrow Control and Close Button
  $(document).on("click", ".photogallery__modal .prev", function () {
    if ($(window).width() > 1024) {
      $(".photogallery .gallery-top .prev").click();
      updateModalDesk();
    }
  });

  $(document).on("click", ".photogallery__modal .next", function () {
    if ($(window).width() > 1024) {
      $(".photogallery .gallery-top .next").click();
      updateModalDesk();
    }
  });

  $(document).on("click", ".photogallery__modal .modal__close", function () {
    $(".photogallery__modal").fadeOut();
    $("body").css("height", "");
    $("body").css("overflow", "");
  });

  //RESIZE MANAGEMENT
  $(window).on("resize", function () {
    if ($(window).width() !== windowPhoto) {
      windowPhoto = $(window).width();
      pictureSlider();

      if ($(window).width() > 1024) {
        $(".photogallery .gallery-top").removeClass("gallery__modal--mobile");
        $(".photogallery .gallery-top .photogallery__pagination").removeClass(
          "gallery__modal-paginator--mobile"
        );
        $(".photogallery .gallery-top .photogallery__expand .icon")
          .addClass("icon-expand")
          .removeClass("icon-close");
      } else {
        $(".photogallery .gallery__modal--mobile").css(
          "height",
          window.innerHeight
        );
        $(".photogallery__modal").hide();
      }
    }
  });

  /****************************************************
   * Functions
   ****************************************************/

  // Create Swipers
  function pictureSlider() {
    // destroy and initialize again
    if ($(".photogallery .swiper-slide-active").length > 0) {
      $(".gallery-top")[0].swiper.destroy();
      $(".gallery-thumbs")[0].swiper.destroy();
      addBg($(window).width());
    }

    // Swiper bottom (thumbs) spacing breakpoint
    var spacingThumbs;
    if ($(window).width() <= 1024) {
      spacingThumbs = 9;
    } else {
      spacingThumbs = 39;
    }

    // Swiper bottom (thumbs) new instance
    var galleryThumbs = new Swiper(".photogallery .gallery-thumbs", {
      spaceBetween: spacingThumbs,
      slidesPerView: "auto",
      freeMode: true,
      watchSlidesVisibility: true,
      watchSlidesProgress: true,
    });

    // Swiper top (gallery) new instance
    if ($(window).width() <= 1024) {
      var galleryTop = new Swiper(".photogallery .gallery-top", {
        spaceBetween: 10,
        paginationClickable: true,
        autoResize: true,
        pagination: {
          el: ".photogallery .gallery-top [data-pagination]",
          clickable: true,
        },
        thumbs: {
          swiper: galleryThumbs,
        },
        on: {
          transitionEnd: function () {
            var active = $(
              ".photogallery .gallery-top .swiper-slide-active"
            ).attr("data-slide-number");
            $(
              ".photogallery .gallery-thumbs .swiper-slide-visible:nth-child(" +
                active +
                ")"
            ).addClass("swiper-slide-thumb-active");
            updateModalDesk();
            textUpdate();
          },
          init: function () {
            $(
              ".photogallery .gallery-thumbs .swiper-slide-visible:nth-child(1)"
            ).addClass("swiper-slide-thumb-active");
            textUpdate();
          },
        },
      });
    } else {
      var galleryTop = new Swiper(".photogallery .gallery-top", {
        spaceBetween: 10,
        simulateTouch: false,
        on: {
          transitionEnd: function () {
            var active = $(
              ".photogallery .gallery-top .swiper-slide-active"
            ).attr("data-slide-number");
            $(".photogallery .gallery-top [data-number] .current").text(active);
            updateModalDesk();
            textUpdate();
          },
          transitionStart: function () {
            var active = $(
              ".photogallery .gallery-top .swiper-slide-active"
            ).attr("data-slide-number");
            $(
              ".photogallery .gallery-thumbs .swiper-slide-visible:nth-child(" +
                active +
                ")"
            ).addClass("swiper-slide-thumb-active");
          },
          init: function () {
            $(
              ".photogallery .gallery-thumbs .swiper-slide-visible:nth-child(1)"
            ).addClass("swiper-slide-thumb-active");
            textUpdate();
          },
        },
        navigation: {
          nextEl: ".photogallery .gallery-top [data-next-small]",
          prevEl: ".photogallery .gallery-top [data-prev-small]",
        },
        thumbs: {
          swiper: galleryThumbs,
        },
      });
      var active = $(".photogallery .gallery-top .swiper-slide-active").attr(
        "data-slide-number"
      );
      var total = $(
        ".photogallery .gallery-top .swiper-slide:not(.swiper-slide-duplicate)"
      ).length;

      $(".photogallery .gallery-top [data-number] .current").text(active);
      $(".photogallery .gallery-top [data-number] .total").text(total);
    }

    // Sync scroll of sliders
    if (typeof galleryTop.controller !== "undefined") {
      galleryTop.controller.control = galleryThumbs;
    }
  }

  // Refresh images on modal picture expand
  function updateModalDesk() {
    // Refresh Modal image with the active one
    var actualImg = $(".photogallery .gallery-top .swiper-slide-active").css(
      "background-image"
    );

    $(".photogallery__modal .picture").css("background-image", actualImg);

    // Refresh Counters on Modal
    $(".photogallery__modal .current").text(
      $(".photogallery .gallery-top .current").text()
    );
    $(".photogallery__modal .total").text(
      $(".photogallery .gallery-top .total").text()
    );
  }

  // Update Middle Text
  function textUpdate() {
    var activeText = $(
      ".photogallery .gallery-top .swiper-slide-active .swiper-text"
    ).text();
    $(".photogallery .text-middle").text(activeText);
  }
});
