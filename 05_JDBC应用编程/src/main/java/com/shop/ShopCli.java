package com.shop;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public final class ShopCli {
    private static final List<Menu> CUSTOMER_MENUS = List.of(
            new Menu("商品浏览", List.of("浏览在售商品", "按分类查询", "查看商品详情")),
            new Menu("购物车", List.of("查看购物车", "添加商品", "修改数量", "移除商品", "结算选中的商品")),
            new Menu("我的订单", List.of("直接下单", "查看订单列表", "查看订单详情", "取消订单", "确认收货"))
    );
    private static final List<Menu> ADMIN_MENUS = List.of(
            new Menu("用户管理", List.of("查询用户", "创建顾客账号", "修改用户资料", "删除用户")),
            new Menu("商品与库存管理", List.of("查询分类", "新增分类", "修改分类", "删除分类",
                    "查询商品", "新增商品", "修改商品", "商品上下架", "调整库存")),
            new Menu("订单管理", List.of("查询订单", "查看订单详情", "发货")),
            new Menu("销售统计", List.of("按商品统计销量与销售额"))
    );

    private final BufferedReader input;
    private final PrintWriter output;
    private final Console console;

    // 对应模块实现后，由构造方法接收并初始化这些字段。
    // private final UserService userService;
    // private final CatalogService catalogService;
    // private final CartService cartService;
    // private final OrderService orderService;
    // private final SalesService salesService;

    ShopCli(BufferedReader input, PrintWriter output, Console console) {
        this.input = input;
        this.output = output;
        this.console = console;
    }

    public static void main(String[] args) throws IOException {
        Console console = System.console();
        PrintWriter output = console == null ? new PrintWriter(System.out, true) : console.writer();
        // 模块实现后，在这里创建各 Service 并传给 ShopCli。
        new ShopCli(new BufferedReader(new InputStreamReader(System.in)), output, console).run();
    }

    void run() throws IOException {
        while (true) {
            int choice = choose("网上购物系统", List.of("登录", "注册"), "退出程序");
            if (choice <= 0) {
                return;
            }
            authenticate(choice == 2);
        }
    }

    private void authenticate(boolean registering) throws IOException {
        String account = readLine("账号：");
        if (account == null) {
            return;
        }
        account = account.strip();
        if (account.isEmpty()) {
            output.println("账号不能为空。");
            return;
        }

        while (true) {
            char[] password = readPassword("密码：");
            if (password == null) {
                return;
            }
            try {
                if (password.length == 0) {
                    output.println("密码不能为空。");
                    continue;
                }
                if (registering) {
                    char[] confirmation = readPassword("确认密码：");
                    if (confirmation == null) {
                        return;
                    }
                    boolean matches = Arrays.equals(password, confirmation);
                    Arrays.fill(confirmation, '\0');
                    if (!matches) {
                        output.println("两次密码不一致，请重新输入。");
                        continue;
                    }
                }
                // if (registering) {
                //     userService.registerCustomer(account, password);
                // } else {
                //     signedInUser = userService.login(account, password);
                // }
                output.println("账号服务待接入。");
            } finally {
                Arrays.fill(password, '\0');
            }
            // 登录成功后，根据返回的用户角色调用 showRoleMenu(...)。
            return;
        }
    }

    private void showRoleMenu(Role role) throws IOException {
        boolean administrator = role == Role.ADMIN;
        String title = administrator ? "管理员主菜单" : "顾客主菜单";
        List<Menu> menus = administrator ? ADMIN_MENUS : CUSTOMER_MENUS;
        List<String> labels = menus.stream().map(Menu::title).toList();
        while (true) {
            int choice = choose(title, labels, "退出登录");
            if (choice <= 0) {
                return;
            }
            Menu menu = menus.get(choice - 1);
            int action = choose(menu.title(), menu.options(), "返回上一级");
            while (action > 0) {
                // 按 choice 和 action 调用对应 Service。购物车首项可调用 cartService.getCart(...)。
                output.println("该操作待接入。");
                action = choose(menu.title(), menu.options(), "返回上一级");
            }
            if (action < 0) {
                return;
            }
        }
    }

    private int choose(String title, List<String> options, String exitLabel) throws IOException {
        while (true) {
            output.println();
            output.println(title);
            for (int i = 0; i < options.size(); i++) {
                output.printf("%d. %s%n", i + 1, options.get(i));
            }
            output.printf("0. %s%n", exitLabel);

            String line = readLine("请选择：");
            if (line == null) {
                return -1;
            }
            try {
                int choice = Integer.parseInt(line.strip());
                if (choice >= 0 && choice <= options.size()) {
                    return choice;
                }
            } catch (NumberFormatException ignored) {
                // 无效编号统一提示后重新显示菜单。
            }
            output.println("请输入菜单中的编号。");
        }
    }

    private String readLine(String prompt) throws IOException {
        if (console != null) {
            return console.readLine("%s", prompt);
        }
        output.print(prompt);
        output.flush();
        return input.readLine();
    }

    private char[] readPassword(String prompt) throws IOException {
        if (console != null) {
            return console.readPassword("%s", prompt);
        }
        String line = readLine(prompt);
        return line == null ? null : line.toCharArray();
    }

    private enum Role { CUSTOMER, ADMIN }

    private record Menu(String title, List<String> options) {
    }
}
