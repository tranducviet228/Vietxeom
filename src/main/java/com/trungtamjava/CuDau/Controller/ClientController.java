package com.trungtamjava.CuDau.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.event.PublicInvocationEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.trungtamjava.CuDau.Dto.CategoryDto;
import com.trungtamjava.CuDau.Dto.ProductBillDto;
import com.trungtamjava.CuDau.Dto.ProductDto;
import com.trungtamjava.CuDau.Service.CategoryService;
import com.trungtamjava.CuDau.Service.ProductBillService;
import com.trungtamjava.CuDau.Service.ProductService;
import com.trungtamjava.CuDau.Service.UserService;

@Controller
public class ClientController {

	@Autowired
	UserService userService;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	ProductBillService productBillService;
	@GetMapping(value = "/index")
	public String products(HttpServletRequest request) {
		String nameCate= request.getParameter("nameCate");
		String namePro= request.getParameter("namePro");
//		Integer page=Integer.valueOf(request.getParameter("page"));
		List<CategoryDto> listC= categoryService.getAllCate();
		List<ProductDto> listP= productService.getAllPro();
		
		
		
		request.setAttribute("listP", listP);
		request.setAttribute("listC", listC);
		return "index";
		
	}
	
	@GetMapping(value = "/product")
	public String product(HttpServletRequest request, @RequestParam(value  = "id", required = true) Long id) {
		ProductDto productDto= productService.get(id);
		request.setAttribute("p", productDto);
		return "client/product-details";
		
	}
	@GetMapping(value = "/client/cart")
	public String addCart(HttpServletRequest request, @RequestParam(value = "pId") Long pId, HttpSession session) {
		
		ProductDto productDto= productService.get(pId);
		Object object= session.getAttribute("cart");
		if(object == null) {
			ProductBillDto productBillDto= new ProductBillDto();
			productBillDto.setProductDto(productDto);
			productBillDto.setQuantity(1);
			productBillDto.setUnitPrice(productDto.getPrice());
			Map<Long, ProductBillDto> map=new HashMap<Long, ProductBillDto>();
			map.put(pId, productBillDto);
			session.setAttribute("cart", map);
		}
	    else {
			Map<Long, ProductBillDto> map = (Map<Long, ProductBillDto>) object;
			ProductBillDto productBillDto= map.get(pId);
			if(productBillDto == null) {
				 productBillDto= new ProductBillDto();
				 productBillDto.setProductDto(productDto);
				 productBillDto.setQuantity(1);
				 productBillDto.setUnitPrice(productDto.getPrice());
				 map.put(pId, productBillDto);
				 
			}
			
			else {
				if(productBillDto.getQuantity()<productDto.getAmount()) {
					productBillDto.setQuantity(productBillDto.getQuantity()+1);
				}
				else {
					productBillDto.setQuantity(productBillDto.getQuantity());
				}
			}
			
			session.setAttribute("cart", map);
			
			
			
			
		}
	    
		return "redirect:/cart";
		
	}
	@GetMapping(value = "/cart")
	public String cart(HttpSession session) {
		Object object2= session.getAttribute("cart");
		Map<Long, ProductBillDto> map2= (Map<Long, ProductBillDto>) object2;
		
		Long sum=(long) 0;
		for(Entry<Long, ProductBillDto> entry: map2.entrySet()) {
			sum = sum + entry.getValue().getQuantity()*entry.getValue().getUnitPrice();
		}
		session.setAttribute("total", sum);
		return"/client/cart";
	}
	
	@GetMapping(value = "/delete/cart")
	public String deleteCart(HttpSession session , @RequestParam(name = "key", required = true) Long key) {
		Object object=session.getAttribute("cart");
		Map<Long, ProductBillDto> map= (Map<Long, ProductBillDto>) object;
		map.remove(key);
		session.setAttribute("cart", map);
		return "redirect:/cart";
		
	}
	
}
