package com.project.supermarket.productos.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.supermarket.productos.Entity.Empleado;
import com.project.supermarket.productos.service.EmpleadoService;
import com.project.supermarket.productos.util.paginacion.PageRender;

@Controller
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    // @GetMapping({ "/", "/listar", "" })
    @GetMapping("/empleados")
    public String listarEmpleados(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Pageable pageRequest = PageRequest.of(page, 5);
        Page<Empleado> empleados = empleadoService.findAll(pageRequest);
        PageRender<Empleado> pageRender = new PageRender<>("/empleados", empleados);

        model.addAttribute("titulo", "Listado de empleados");
        model.addAttribute("empleados", empleados);
        model.addAttribute("page", pageRender);

        return "listar_empleados";
    }

    @GetMapping("/empleados/ver/{id}")
    public String verDetallesDelEmpleado(@PathVariable(value = "id") Long id, Map<String, Object> modelo,
            RedirectAttributes flash) {
        Empleado empleado = empleadoService.findOne(id);
        if (empleado == null) {
            flash.addFlashAttribute("error", "El empleado no existe en la base de datos");
            return "redirect:/empleados";
        }
        modelo.put("empleado", empleado);
        modelo.put("titulo", "Detalles del empleado " + empleado.getNombre());
        return "ver_detalles_empleado";
    }

    @GetMapping("/empleados/form")
    public String mostrarFormularioDeRegistrarCliente(Map<String, Object> modelo) {
        Empleado empleado = new Empleado();
        modelo.put("empleado", empleado);
        modelo.put("titulo", "Registro de empleados");
        return "form_empleados";
    }

    @PostMapping("/empleados/form")
    public String guardarEmpleado(@Valid Empleado empleado, BindingResult result, Model modelo,
            RedirectAttributes flash, SessionStatus status) {
        if (result.hasErrors()) {
            modelo.addAttribute("titulo", "Registro de cliente");
            return "form_empleados";
        }

        String mensaje = (empleado.getId() != null) ? "El empleado ha sido editato con exito"
                : "Empleado registrado con exito";

        empleadoService.save(empleado);
        status.setComplete();
        flash.addFlashAttribute("success", mensaje);
        return "redirect:/empleados";
    }

    @GetMapping("/empleados/form/{id}")
    public String editarEmpleado(@PathVariable(value = "id") Long id, Map<String, Object> modelo,
            RedirectAttributes flash) {
        Empleado empleado = null;
        if (id > 0) {
            empleado = empleadoService.findOne(id);
            if (empleado == null) {
                flash.addFlashAttribute("error", "El ID del empleado no existe en la base de datos");
                return "redirect:/empleados";
            }
        } else {
            flash.addFlashAttribute("error", "El ID del empleado no puede ser cero");
            return "redirect:/editar";
        }

        modelo.put("empleado", empleado);
        modelo.put("titulo", "Edición de empleado");
        return "form_empleados";
    }

    @GetMapping("/empleados/eliminar/{id}")
    public String eliminarCliente(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
        if (id > 0) {
            empleadoService.delete(id);
            flash.addFlashAttribute("success", "Empleado eliminado con exito");
        }
        return "redirect:/empleados";
    }

}
