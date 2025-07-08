document.addEventListener("DOMContentLoaded", function() {
    // Add event listener to "Add to Cart" button
    var addToCartButton = document.getElementById("add-to-cart");
    if (addToCartButton) {
        addToCartButton.addEventListener("click", function() {
            alert("Add to cart successful!");
        });
    }
});
