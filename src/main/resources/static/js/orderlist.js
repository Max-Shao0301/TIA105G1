
let currentPage = 1;
let pageSize = 5;
// 設定初始排序為訂單編號
let sortColumn = 'orderId';
// 設定初始排序方向為遞減
let sortDirection = 'desc';

function displayOrders() {
    const startIndex = (currentPage - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    let currentOrders = [...orders];

    // 因為已設定初始排序欄位，這裡直接排序
    currentOrders.sort((a, b) => {
        const aValue = a[sortColumn];
        const bValue = b[sortColumn];

        if (sortColumn === 'orderDate') {
            const dateA = new Date(aValue);
            const dateB = new Date(bValue);
            return sortDirection === 'asc' ? dateA - dateB : dateB - dateA;
        } else if (sortColumn === 'status') {
            const statusOrder = { '已完成': 0, '進行中': 1, '已取消': 2 };
            return sortDirection === 'asc'
                ? statusOrder[aValue] - statusOrder[bValue]
                : statusOrder[bValue] - statusOrder[aValue];
        } else if (sortColumn === 'orderId') {
            const numA = parseInt(aValue);
            const numB = parseInt(bValue);
            return sortDirection === 'asc' ? numA - numB : numB - numA;
        } else {
            return sortDirection === 'asc'
                ? aValue.localeCompare(bValue, 'zh-Hant')
                : bValue.localeCompare(aValue, 'zh-Hant');
        }
    });

    currentOrders = currentOrders.slice(startIndex, endIndex);

    const tableBody = document.querySelector('#orderTable tbody');
    tableBody.innerHTML = '';


    document.getElementById('pageNumber').textContent = `第 ${currentPage} 頁`;
    document.getElementById('prevPage').disabled = currentPage === 1;
    document.getElementById('nextPage').disabled = currentPage * pageSize >= orders.length;
}

function sortOrders(column) {
    if (sortColumn === column) {
        sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
        sortColumn = column;
        sortDirection = 'asc';
    }
    displayOrders();
}

function showDetails(orderId) {
    window.location.href = `./order.html?orderId=${orderId}`;
}

function changePage(direction) {
    currentPage += direction;
    displayOrders();
}

function updatePageSize() {
    pageSize = parseInt(document.getElementById('pageSize').value);
    currentPage = 1;
    displayOrders();
}

// 初始化顯示
displayOrders();

