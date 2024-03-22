declare namespace API {
  type BaseResponseBoolean_ = {
    code?: number;
    data?: boolean;
    message?: string;
  };
  type BaseResponseGeneratorVO_ = {
    code?: number;
    data?: GeneratorVO;
    message?: string;
  };
  type BaseResponseInteger = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponseLoginUserVO = {
    code?: number;
    data?: LoginUserVO;
    message?: string;
  };

  type BaseResponseLong_ = {
    code?: number;
    data?: string;
    message?: string;
  };
  type BaseResponsePageGenerator_ = {
    code?: number;
    data?: PageGenerator_;
    message?: string;
  };
  type BaseResponsePageGeneratorVO_ = {
    code?: number;
    data?: PageGeneratorVO_;
    message?: string;
  };
  type BaseResponsePageUser = {
    code?: number;
    data?: PageUser_;
    message?: string;
  };
  type BaseResponsePageUserVO_ = {
    code?: number;
    data?: PageUserVO_;
    message?: string;
  };
  type BaseResponsePagePostVO_ = {
    code?: number;
    data?: PagePostVO_;
    message?: string;
  };


  type BaseResponsePostVO_ = {
    code?: number;
    data?: PostVO;
    message?: string;
  };

  type BaseResponseString_ = {
    code?: number;
    data?: string;
    message?: string;
  };

  type BaseResponseUser_ = {
    code?: number;
    data?: User;
    message?: string;
  };

  type BaseResponseUserVO_ = {
    code?: number;
    data?: UserVO;
    message?: string;
  };

  type checkParams = {
    timestamp: string;
    nonce: string;
    signature: string;
    echostr: string;
  };

  type DeleteRequest = {
    id?: string;
  };
  type FileConfig = {
    files?: FileInfo[];
    inputRootPath?: string;
    outputRootPath?: string;
    sourceRootPath?: string;
    type?: string;
  };
  type FileInfo = {
    condition?: string;
    files?: FileInfo[];
    generateType?: string;
    groupKey?: string;
    groupName?: string;
    inputPath?: string;
    outputPath?: string;
    type?: string;
  };
  type Generator = {
    author?: string;
    basePackage?: string;
    createTime?: string;
    description?: string;
    distPath?: string;
    fileConfig?: string;
    id?: string;
    isDelete?: number;
    modelConfig?: string;
    name?: string;
    picture?: string;
    status?: number;
    tags?: string;
    updateTime?: string;
    userId?: string;
    version?: string;
  };
  type GeneratorAddRequest = {
    author?: string;
    basePackage?: string;
    description?: string;
    distPath?: string;
    fileConfig?: FileConfig;
    modelConfig?: ModelConfig;
    name?: string;
    picture?: string;
    status?: number;
    tags?: string[];
    version?: string;
  };
  type GeneratorEditRequest = {
    author?: string;
    basePackage?: string;
    description?: string;
    distPath?: string;
    fileConfig?: FileConfig;
    id?: string;
    modelConfig?: ModelConfig;
    name?: string;
    picture?: string;
    tags?: string[];
    version?: string;
  };
  type GeneratorQueryRequest = {
    author?: string;
    basePackage?: string;
    current?: number;
    description?: string;
    distPath?: string;
    id?: string;
    name?: string;
    notId?: string;
    orTags?: string[];
    pageSize?: number;
    searchText?: string;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    tags?: string[];
    userId?: string;
    version?: string;
  };
  type GeneratorUpdateRequest = {
    author?: string;
    basePackage?: string;
    description?: string;
    distPath?: string;
    fileConfig?: FileConfig;
    id?: string;
    modelConfig?: ModelConfig;
    name?: string;
    picture?: string;
    status?: number;
    tags?: string[];
    version?: string;
  }
  type GeneratorVO = {
    author?: string;
    basePackage?: string;
    createTime?: string;
    description?: string;
    distPath?: string;
    fileConfig?: FileConfig;
    id?: string;
    modelConfig?: ModelConfig;
    name?: string;
    picture?: string;
    status?: number;
    tags?: string[];
    updateTime?: string;
    user?: UserVO;
    userId?: string;
    version?: string;
  };
  type getGeneratorVOByIdUsingGETParams = {
    /** id */
    id?: string;
  };
  type getUserByIdUsingGETParams = {
    /** id */
    id?: string;
  };
  type getUserVOByIdUsingGETParams = {
    /** id */
    id?: string;
  };


  type LoginUserVO = {
    createTime?: string;
    id?: string;
    updateTime?: string;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type ModelConfig = {
    models?: ModelInfo[];
  };

  type ModelInfo = {
    abbr?: string;
    allArgsStr?: string;
    condition?: string;
    defaultValue?: Record<string, any>;
    description?: string;
    fieldName?: string;
    groupKey?: string;
    groupName?: string;
    models?: ModelInfo[];
    type?: string;
  }

  type OrderItem = {
    asc?: boolean;
    column?: string;
  };

  type PageGenerator_ = {
    countId?: string;
    current?: string;
    maxLimit?: string;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: string;
    records?: Generator[];
    searchCount?: boolean;
    size?: string;
    total?: string;
  };

  type PageGeneratorVO_ = {
    countId?: string;
    current?: string;
    maxLimit?: string;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: string;
    records?: GeneratorVO[];
    searchCount?: boolean;
    size?: string;
    total?: string;
  };

  type PageUser_ = {
    countId?: string;
    current?: string;
    maxLimit?: string;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: string;
    records?: User[];
    searchCount?: boolean;
    size?: string;
    total?: string;
  };
  type PageUserVO_ = {
    countId?: string;
    current?: string;
    maxLimit?: string;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: string;
    records?: UserVO[];
    searchCount?: boolean;
    size?: string;
    total?: string;
  };
  type uploadFileUsingPOSTParams = {
    biz?: string;
  };


  // type PagePostVO = {
  //   records?: PostVO[];
  //   total?: string;
  //   size?: string;
  //   current?: string;
  //   orders?: OrderItem[];
  //   optimizeCountSql?: boolean;
  //   searchCount?: boolean;
  //   optimizeJoinOfCountSql?: boolean;
  //   countId?: string;
  //   maxLimit?: string;
  //   pages?: string;
  // };
  //
  // type PageUser = {
  //   records?: User[];
  //   total?: string;
  //   size?: string;
  //   current?: string;
  //   orders?: OrderItem[];
  //   optimizeCountSql?: boolean;
  //   searchCount?: boolean;
  //   optimizeJoinOfCountSql?: boolean;
  //   countId?: string;
  //   maxLimit?: string;
  //   pages?: string;
  // };
  //
  // type PageUserVO = {
  //   records?: UserVO[];
  //   total?: string;
  //   size?: string;
  //   current?: string;
  //   orders?: OrderItem[];
  //   optimizeCountSql?: boolean;
  //   searchCount?: boolean;
  //   optimizeJoinOfCountSql?: boolean;
  //   countId?: string;
  //   maxLimit?: string;
  //   pages?: string;
  // };
  //
  // type PostAddRequest = {
  //   title?: string;
  //   content?: string;
  //   tags?: string[];
  // };
  //
  // type PostEditRequest = {
  //   id?: string;
  //   title?: string;
  //   content?: string;
  //   tags?: string[];
  // };
  //
  // type PostFavourAddRequest = {
  //   postId?: string;
  // };
  //
  // type PostFavourQueryRequest = {
  //   current?: string;
  //   pageSize?: string;
  //   sortField?: string;
  //   sortOrder?: string;
  //   postQueryRequest?: PostQueryRequest;
  //   userId?: string;
  // };
  //
  // type PostQueryRequest = {
  //   current?: string;
  //   pageSize?: string;
  //   sortField?: string;
  //   sortOrder?: string;
  //   id?: string;
  //   notId?: string;
  //   searchText?: string;
  //   title?: string;
  //   content?: string;
  //   tags?: string[];
  //   orTags?: string[];
  //   userId?: string;
  //   favourUserId?: string;
  // };
  //
  // type PostThumbAddRequest = {
  //   postId?: string;
  // };
  //
  // type PostUpdateRequest = {
  //   id?: string;
  //   title?: string;
  //   content?: string;
  //   tags?: string[];
  // };
  //
  // type PostVO = {
  //   id?: string;
  //   title?: string;
  //   content?: string;
  //   thumbNum?: number;
  //   favourNum?: number;
  //   userId?: string;
  //   createTime?: string;
  //   updateTime?: string;
  //   tagList?: string[];
  //   user?: UserVO;
  //   hasThumb?: boolean;
  //   hasFavour?: boolean;
  // };
  //
  // type uploadFileParams = {
  //   uploadFileRequest: UploadFileRequest;
  // };
  //
  // type UploadFileRequest = {
  //   biz?: string;
  // };

  type User = {
    createTime?: string;
    id?: string;
    isDelete?: number;
    updateTime?: string;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userPassword?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserAddRequest = {
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userRole?: string;
  };
  type UserLoginRequest = {
    userAccount?: string;
    userPassword?: string;
  };


  type UserQueryRequest = {
    current?: number;
    id?: string;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserRegisterRequest = {
    checkPassword?: string;
    userAccount?: string;
    userPassword?: string;
  };

  type UserUpdateMyRequest = {

    userAvatar?: string;
    userName?: string;
    userProfile?: string;
  };

  type UserUpdateRequest = {
    id?: string;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserVO = {
    createTime?: string;
    id?: string;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;

  };
}