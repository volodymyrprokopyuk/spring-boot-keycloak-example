package org.vld.template.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest

@Controller
class WebController {

    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("title", "Home")
        return "index"
    }

    @GetMapping("/products")
    fun products(model: Model): String {
        model.addAttribute("title", "Products")
        return "products"
    }

    @GetMapping("/customers")
    fun customers(model: Model): String {
        model.addAttribute("title", "Customers")
        return "customers"
    }

    @GetMapping("/logout")
    fun logout(request: HttpServletRequest): String {
        request.logout()
        return "redirect:/"
    }
}
