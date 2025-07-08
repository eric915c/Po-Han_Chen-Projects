document.addEventListener("DOMContentLoaded", function() { //borrow from ChatGPT
    // Add event listener to all "Remove" buttons
    var removeButtons = document.querySelectorAll("#cart button");
    removeButtons.forEach(function(button) {
        button.addEventListener("click", function() {
            removeItem(button.parentElement);
            checkCart(); // Check the cart after removing an item
        });
    });

    // Add event listener to "Checkout" button
    var checkoutButton = document.getElementById("checkout");
    if (checkoutButton) {
        checkoutButton.addEventListener("click", function() {
            checkout();
        });
    }
});

// Function to remove an item from the cart
function removeItem(item) {
    item.remove();
}

// Function to perform checkout action
function checkout() {
    var cartItems = document.querySelectorAll("#cart li");
    if (cartItems.length === 0) {
        alert("Your cart is empty!");
    } else {
        alert("Checkout successful!");
        // Clear the cart
        var cart = document.getElementById("cart");
        cart.innerHTML = "";
    }
}

// Function to check if the cart is empty and update button state
function checkCart() {
    var checkoutButton = document.getElementById("checkout");
    var cartItems = document.querySelectorAll("#cart li");
    checkoutButton.disabled = cartItems.length === 0;
    if (cartItems.length === 0) {
        alert("Your cart is empty!"); // Check the cart after removing the last item
    }
}

// Initial state check
checkCart();
