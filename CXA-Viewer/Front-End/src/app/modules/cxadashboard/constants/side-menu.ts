export const sideMenu = [
    {
        "text": "Customer",
        "icon": "people",
        "routerLink": "/customer/manage"
    },
    {
        "text": "Supplier",
        "icon": "supervised_user_circle",
        "routerLink": "/supplier/manage"
    },
    {
        "text": "Suit",
        "icon": "inventory_2",
        "children": [{
            "text": "Category",
            "icon": "category",
            "routerLink": "/product/category"
        },
        {
            "text": "Sub Category",
            "icon": "layers",
            "routerLink": "/product/sub-category"
        },
        {
            "text": "Product",
            "icon": "all_inbox",
            "routerLink": "/product/manage"
        }
        ]
    }
];