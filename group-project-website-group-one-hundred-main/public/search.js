document.addEventListener("DOMContentLoaded", function() {
    // Add event listener to the filter form
    document.getElementById("filter-form").addEventListener("submit", function(event) {
        event.preventDefault(); // Prevent default form submission
        
        // Perform filtering action
        filterResults();
    });

    // Add event listener to the filter select input
    document.getElementById("price-filter").addEventListener("change", function() {
        // Perform filtering action when the selection changes
        filterResults();
    });
});

// Function to filter search results based on price
function filterResults() {
    var priceFilter = document.getElementById("price-filter").value;
    var searchResults = document.querySelectorAll("#search-results li");

    // Convert NodeList to array for easier manipulation
    searchResults = Array.from(searchResults);

    // Sort search results based on price
    if (priceFilter === "low-to-high") {
        searchResults.sort(function(a, b) {
            var priceA = parseFloat(a.querySelector("p").innerText.replace("$", ""));
            var priceB = parseFloat(b.querySelector("p").innerText.replace("$", ""));
            return priceA - priceB;
        });
    } else if (priceFilter === "high-to-low") {
        searchResults.sort(function(a, b) {
            var priceA = parseFloat(a.querySelector("p").innerText.replace("$", ""));
            var priceB = parseFloat(b.querySelector("p").innerText.replace("$", ""));
            return priceB - priceA;
        });
    }

    // Re-append sorted search results to the list
    var ul = document.getElementById("search-results");
    ul.innerHTML = ""; // Clear existing results
    searchResults.forEach(function(item) {
        ul.appendChild(item);
    });
}
