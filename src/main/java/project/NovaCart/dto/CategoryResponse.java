package project.NovaCart.dto;

public class CategoryResponse {

    private Long id;

    private String name;

    private String slug;

    private String description;

    private Long createdByAdminId;

    public CategoryResponse() {
    }

    public CategoryResponse(Long id, String name,
                            String slug,
                            String description,
                            Long createdByAdminId) {

        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.createdByAdminId = createdByAdminId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatedByAdminId() {
        return createdByAdminId;
    }

    public void setCreatedByAdminId(Long createdByAdminId) {
        this.createdByAdminId = createdByAdminId;
    }
}