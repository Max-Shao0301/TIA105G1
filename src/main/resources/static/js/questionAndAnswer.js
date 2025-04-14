document.querySelectorAll('.accordion-collapse').forEach(function (collapseEl) {
  collapseEl.addEventListener('show.bs.collapse', function () {
    const button = this.previousElementSibling.querySelector('.accordion-button');
    const icon = button.querySelector('.faq-icon');
    if (icon) {
      icon.classList.remove('icofont-caret-right');
      icon.classList.add('icofont-caret-down');
    }
  });

  collapseEl.addEventListener('hide.bs.collapse', function () {
    const button = this.previousElementSibling.querySelector('.accordion-button');
    const icon = button.querySelector('.faq-icon');
    if (icon) {
      icon.classList.remove('icofont-caret-down');
      icon.classList.add('icofont-caret-right');
    }
  });
});
