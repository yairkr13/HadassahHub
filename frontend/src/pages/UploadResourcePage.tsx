import React, { useState, useEffect } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Card, Button, Input } from '@/components/ui';
import { useCourse } from '@/hooks/useCourses';
import { resourceService } from '@/services/api/resource.service';
import { CreateResourceRequest, ResourceType, getResourceTypeDisplayName } from '@/types/resource.types';

export const UploadResourcePage: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const courseId = id ? parseInt(id, 10) : 0;
  const queryClient = useQueryClient();

  // Fetch course details to show course name
  const { data: course, isLoading: courseLoading } = useCourse(courseId);

  const [uploadMode, setUploadMode] = useState<'url' | 'file'>('url');
  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  const [formData, setFormData] = useState<CreateResourceRequest>({
    courseId: courseId,
    title: '',
    type: ResourceType.EXAM,
    url: '',
    academicYear: '',
    examTerm: '',
  });

  const [errors, setErrors] = useState<Partial<Record<keyof CreateResourceRequest | 'file', string>>>({});

  // Update courseId when params change
  useEffect(() => {
    if (courseId) {
      setFormData(prev => ({ ...prev, courseId }));
    }
  }, [courseId]);

  const uploadMutation = useMutation({
    mutationFn: resourceService.createResource,
    onSuccess: () => {
      // Invalidate and refetch relevant queries
      queryClient.invalidateQueries({ queryKey: ['resources'] });
      queryClient.invalidateQueries({ queryKey: ['course-resources'] });

      // Navigate back to course detail page
      navigate(`/courses/${courseId}`);
    },
    onError: (error: any) => {
      setErrors({
        title: error.response?.data?.message || 'Upload failed. Please try again.',
      });
    },
  });

  const validateForm = (): boolean => {
    const newErrors: Partial<Record<keyof CreateResourceRequest | 'file', string>> = {};

    if (!formData.courseId || formData.courseId === 0) {
      newErrors.courseId = 'Course is required';
    }

    if (!formData.title.trim()) {
      newErrors.title = 'Resource title is required';
    }

    // Validate based on upload mode
    if (uploadMode === 'url') {
      if (!formData.url?.trim()) {
        newErrors.url = 'Resource URL is required';
      } else {
        // Basic URL validation
        try {
          new URL(formData.url);
        } catch {
          newErrors.url = 'Please enter a valid URL';
        }
      }
    } else {
      // File mode
      if (!selectedFile) {
        newErrors.file = 'Please select a file to upload';
      } else {
        // Validate file size (10MB max)
        const maxSize = 10 * 1024 * 1024; // 10MB in bytes
        if (selectedFile.size > maxSize) {
          newErrors.file = 'File size must be less than 10MB';
        }
        
        // Validate file type
        const allowedTypes = [
          'application/pdf',
          'image/png',
          'image/jpeg',
          'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
          'text/plain'
        ];
        if (!allowedTypes.includes(selectedFile.type)) {
          newErrors.file = 'File type not allowed. Please upload PDF, PNG, JPEG, DOCX, or TXT files';
        }
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) return;

    // Clean up form data
    const submitData: CreateResourceRequest = {
      courseId: formData.courseId,
      title: formData.title.trim(),
      type: formData.type,
      academicYear: formData.academicYear?.trim() || undefined,
      examTerm: formData.examTerm?.trim() || undefined,
    };

    // Add url or file based on mode
    if (uploadMode === 'url') {
      submitData.url = formData.url?.trim();
    } else {
      submitData.file = selectedFile || undefined;
    }

    uploadMutation.mutate(submitData);
  };

  const handleChange = (field: keyof CreateResourceRequest, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));

    // Clear error when user starts typing
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: undefined }));
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    setSelectedFile(file);
    
    // Clear file error when user selects a file
    if (file && errors.file) {
      setErrors(prev => ({ ...prev, file: undefined }));
    }
  };

  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  };

  const academicYears = [
    '2024-2025',
    '2023-2024',
    '2022-2023',
    '2021-2022',
  ];

  const examTerms = [
    'Moed A',
    'Moed B',
    'Moed C',
  ];

  if (courseLoading) {
    return (
      <div className="min-h-screen bg-background-soft flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>
    );
  }

  if (!course) {
    return (
      <div className="min-h-screen bg-background-soft flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-text-primary mb-2">
            Course Not Found
          </h2>
          <p className="text-text-secondary mb-4">
            The course you're trying to upload to doesn't exist.
          </p>
          <Link to="/courses">
            <Button variant="primary">Back to Courses</Button>
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background-soft">
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Breadcrumb */}
        <nav className="mb-6">
          <Link
            to={`/courses/${courseId}`}
            className="text-primary hover:text-primary-dark transition-colors"
          >
            ← Back to {course.name}
          </Link>
        </nav>

        {/* Page Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-text-primary mb-2">
            Upload Resource
          </h1>
          <p className="text-text-secondary">
            Share study materials for <span className="font-medium">{course.name}</span>
          </p>
        </div>

        {/* Upload Form */}
        <Card>
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Course Display (Read-only) */}
            <div>
              <label className="block text-sm font-medium text-text-primary mb-2">
                Course
              </label>
              <div className="w-full px-4 py-3 border border-gray-300 rounded-lg bg-gray-50 text-text-secondary">
                {course.name}
              </div>
            </div>

            {/* Resource Title */}
            <div>
              <label className="block text-sm font-medium text-text-primary mb-2">
                Resource Title *
              </label>
              <Input
                type="text"
                value={formData.title}
                onChange={(e) => handleChange('title', e.target.value)}
                placeholder="e.g., Final Exam 2024, Assignment 3 Solution"
                error={errors.title}
              />
            </div>

            {/* Resource Type */}
            <div>
              <label className="block text-sm font-medium text-text-primary mb-3">
                Resource Type *
              </label>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                {Object.values(ResourceType).map((type) => (
                  <button
                    key={type}
                    type="button"
                    onClick={() => handleChange('type', type)}
                    className={`p-3 border rounded-lg text-sm font-medium transition-colors ${formData.type === type
                      ? 'border-primary bg-primary text-white'
                      : 'border-gray-300 bg-white text-text-secondary hover:border-primary'
                      }`}
                  >
                    {getResourceTypeDisplayName(type)}
                  </button>
                ))}
              </div>
            </div>

            {/* Upload Mode Selection */}
            <div>
              <label className="block text-sm font-medium text-text-primary mb-3">
                Upload Method *
              </label>
              <div className="grid grid-cols-2 gap-3">
                <button
                  type="button"
                  onClick={() => {
                    setUploadMode('url');
                    setSelectedFile(null);
                    setErrors(prev => ({ ...prev, file: undefined }));
                  }}
                  className={`p-4 border rounded-lg text-sm font-medium transition-colors ${uploadMode === 'url'
                    ? 'border-primary bg-primary text-white'
                    : 'border-gray-300 bg-white text-text-secondary hover:border-primary'
                    }`}
                >
                  <div className="flex flex-col items-center gap-2">
                    <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1" />
                    </svg>
                    <span>Link URL</span>
                  </div>
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setUploadMode('file');
                    setFormData(prev => ({ ...prev, url: '' }));
                    setErrors(prev => ({ ...prev, url: undefined }));
                  }}
                  className={`p-4 border rounded-lg text-sm font-medium transition-colors ${uploadMode === 'file'
                    ? 'border-primary bg-primary text-white'
                    : 'border-gray-300 bg-white text-text-secondary hover:border-primary'
                    }`}
                >
                  <div className="flex flex-col items-center gap-2">
                    <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                    </svg>
                    <span>Upload File</span>
                  </div>
                </button>
              </div>
            </div>

            {/* Resource URL (only if URL mode) */}
            {uploadMode === 'url' && (
              <div>
                <label className="block text-sm font-medium text-text-primary mb-2">
                  Resource URL *
                </label>
                <Input
                  type="url"
                  value={formData.url || ''}
                  onChange={(e) => handleChange('url', e.target.value)}
                  placeholder="https://example.com/resource.pdf"
                  error={errors.url}
                />
                <p className="mt-1 text-sm text-text-secondary">
                  Link to your resource (Google Drive, Dropbox, etc.)
                </p>
              </div>
            )}

            {/* File Upload (only if file mode) */}
            {uploadMode === 'file' && (
              <div>
                <label className="block text-sm font-medium text-text-primary mb-2">
                  Upload File *
                </label>
                <div className="space-y-3">
                  <div className={`border-2 border-dashed rounded-lg p-6 text-center transition-colors ${errors.file ? 'border-red-300 bg-red-50' : 'border-gray-300 hover:border-primary'
                    }`}>
                    <input
                      type="file"
                      id="file-upload"
                      onChange={handleFileChange}
                      accept=".pdf,.png,.jpg,.jpeg,.docx,.txt"
                      className="hidden"
                    />
                    <label
                      htmlFor="file-upload"
                      className="cursor-pointer flex flex-col items-center"
                    >
                      <svg className="w-12 h-12 text-gray-400 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                      </svg>
                      <span className="text-sm font-medium text-primary hover:text-primary-dark">
                        Click to upload
                      </span>
                      <span className="text-xs text-text-secondary mt-1">
                        PDF, PNG, JPEG, DOCX, or TXT (max 10MB)
                      </span>
                    </label>
                  </div>
                  
                  {selectedFile && (
                    <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg border border-gray-200">
                      <div className="flex items-center gap-3">
                        <svg className="w-8 h-8 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                        </svg>
                        <div>
                          <p className="text-sm font-medium text-text-primary">{selectedFile.name}</p>
                          <p className="text-xs text-text-secondary">{formatFileSize(selectedFile.size)}</p>
                        </div>
                      </div>
                      <button
                        type="button"
                        onClick={() => {
                          setSelectedFile(null);
                          const fileInput = document.getElementById('file-upload') as HTMLInputElement;
                          if (fileInput) fileInput.value = '';
                        }}
                        className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                      >
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                        </svg>
                      </button>
                    </div>
                  )}
                  
                  {errors.file && (
                    <p className="text-sm text-red-600">{errors.file}</p>
                  )}
                </div>
              </div>
            )}

            {/* Academic Year */}
            <div>
              <label className="block text-sm font-medium text-text-primary mb-2">
                Academic Year (Optional)
              </label>
              <select
                value={formData.academicYear}
                onChange={(e) => handleChange('academicYear', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition-colors"
              >
                <option value="">Select academic year</option>
                {academicYears.map((year) => (
                  <option key={year} value={year}>
                    {year}
                  </option>
                ))}
              </select>
            </div>

            {/* Exam Term (only for exams) */}
            {formData.type === ResourceType.EXAM && (
              <div>
                <label className="block text-sm font-medium text-text-primary mb-2">
                  Exam Term (Optional)
                </label>
                <select
                  value={formData.examTerm}
                  onChange={(e) => handleChange('examTerm', e.target.value)}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition-colors"
                >
                  <option value="">Select exam term</option>
                  {examTerms.map((term) => (
                    <option key={term} value={term}>
                      {term}
                    </option>
                  ))}
                </select>
              </div>
            )}

            {/* Form Actions */}
            <div className="flex items-center justify-end gap-4 pt-6">
              <Button
                type="button"
                variant="secondary"
                onClick={() => navigate(`/courses/${courseId}`)}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                variant="primary"
                disabled={uploadMutation.isPending}
              >
                {uploadMutation.isPending ? 'Uploading...' : 'Upload Resource'}
              </Button>
            </div>
          </form>
        </Card>
      </div>
    </div>
  );
};