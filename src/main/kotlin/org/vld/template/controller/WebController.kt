package org.vld.template.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.vld.template.service.CustomerService
import org.vld.template.service.ProductService
import javax.servlet.http.HttpServletRequest

@Controller
class WebController(private val productService: ProductService, private val customerService: CustomerService) {

    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("title", "Home")
        return "index"
    }

    @GetMapping("/products")
    fun products(model: Model): String {
        model.addAttribute("title", productService.findAll())
        return "products"
    }

    @GetMapping("/customers")
    fun customers(model: Model): String {
        model.addAttribute("title", customerService.findAll())
        return "customers"
    }

    @GetMapping("/logout")
    fun logout(request: HttpServletRequest): String {
        request.logout()
        return "redirect:/"
    }
}
