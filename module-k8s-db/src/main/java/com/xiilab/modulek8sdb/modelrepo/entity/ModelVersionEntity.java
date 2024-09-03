package com.xiilab.modulek8sdb.modelrepo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.xiilab.modulecommon.dto.FileInfoDTO;
import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.common.entity.RegUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_MODEL_REPO_VERSION")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ModelVersionEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MODEL_REPO_VERSION_ID")
	private long id;
	@Column(name = "VERSION")
	private String version;
	@Column(name = "MODEL_FILE_NAME")
	private String modelFileName;
	@Column(name = "MODEL_FILE_SIZE")
	private String modelFileSize;
	@ManyToOne
	@JoinColumn(name = "MODEL_REPO_ID")
	private ModelRepoEntity modelRepoEntity;
	@OneToMany(mappedBy = "modelVersionEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<ModelMetaEntity> modelMetaEntities = new ArrayList<>();

	public void setModelFile(String modelFileName, String modelFileSize) {
		this.modelFileName = modelFileName;
		this.modelFileSize = modelFileSize;
	}

	public void setModelMeta(List<FileInfoDTO> metafileList) {

		if (Objects.nonNull(metafileList)) {
			metafileList.forEach(metafile -> {
				ModelMetaEntity modelMetaEntity = ModelMetaEntity.builder()
					.modelFileName(metafile.getFileName())
					.modelFileSize(Long.parseLong(metafile.getSize()))
					.modelVersionEntity(this)
					.build();
				this.modelMetaEntities.add(modelMetaEntity);
			});
		}
	}
	public void setRegUserInfo(RegUser regUser, LocalDateTime regDate, LocalDateTime modDate){
		this.regUser = regUser;
		this.regDate = regDate;
		this.modDate = modDate;
	}
}
