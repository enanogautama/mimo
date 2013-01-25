package controllers;

import binders.DateW;
import java.util.*;

import com.avaje.ebean.Page;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;
import play.mvc.*;
import play.data.*;
import play.*;

import static play.libs.Json.toJson;

import views.html.*;

import models.*;

public class Application extends Controller {
    

    public static Result GO_HOME = redirect(
        routes.Application.list(0, "name", "asc", "")
    );
    
    public static Result index() {
        return GO_HOME;
    }

    public static Result list(int page, String sortBy, String order, String filter) {
        return ok(
            list.render(
                Computer.page(page, 10, sortBy, order, filter),
                sortBy, order, filter
            )
        );
    }
    
    public static Result edit(Long id) {
        Form<Computer> computerForm = form(Computer.class).fill(
            Computer.find.byId(id)
        );
        return ok(
            editForm.render(id, computerForm)
        );
    }
    
    public static Result update(Long id) {
        Form<Computer> computerForm = form(Computer.class).bindFromRequest();

        if(computerForm.hasErrors()) {
            return badRequest(editForm.render(id, computerForm));
        }
        computerForm.get().update(id);
        flash("success", "Computer " + computerForm.get().name + " has been updated");
        return GO_HOME;
    }

    public static Result create() {
        Form<Computer> computerForm = form(Computer.class);
        return ok(
            createForm.render(computerForm)
        );
    }

    public static Result save() {
        Form<Computer> computerForm = form(Computer.class).bindFromRequest();
        if(computerForm.hasErrors()) {
            return badRequest(createForm.render(computerForm));
        }
        computerForm.get().save();
        flash("success", "Computer " + computerForm.get().name + " has been created");
        return GO_HOME;
    }
    
    public static Result delete(Long id) {
        Computer.find.ref(id).delete();
        flash("success", "Computer has been deleted");
        return GO_HOME;
    }
    

    // REST API (is duplicated only for teaching purposes)

    // Companies

    public static Result getCompany(Long companyId) { 
        return ok(toJson(Company.find.byId(companyId)));
    }

    public static Result listCompanies(int page, String sortBy, String order, String filter) { 
        Page<Company> pages = Company.page(page, 10, sortBy, order, filter);

        return ok(toJson(pages.getList()));      
    }

    // Computers
    public static Result restList(int page, String sortBy, String order, String filter) {        
        Page<Computer> pages = Computer.page(page, 10, sortBy, order, filter);

        return ok(toJson(pages.getList()));
    }

    public static Result restSave(String name, DateW introduced, DateW discontinued, Long companyId) {
        System.out.println(name + introduced.getDate() + discontinued.getDate() + companyId);

        Computer computer = new Computer();
        computer.name = name;
        computer.introduced = introduced.getDate();
        computer.discontinued = discontinued.getDate();
        computer.company = Company.find.byId(companyId);

        computer.save();

        return ok(toJson(computer));
    }


    // Documents
    public static Result upload() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart uploadFilePart = body.getFile("upload");

        if (uploadFilePart != null) {
            System.out.println("THis is a multipart request!!");
            
            S3File s3File = new S3File();
            s3File.name = uploadFilePart.getFilename();
            s3File.file = uploadFilePart.getFile();
            s3File.save();

            return ok(toJson(s3File));
        }

        return badRequest(toJson("File upload error"));
    }
}
            
